package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.ComunidadRemoteDataSource
import com.example.mixtapp.data.dtos.ChatDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import com.example.mixtapp.data.dtos.CreateMensajeDto
import com.example.mixtapp.data.dtos.ForoDto
import com.example.mixtapp.data.dtos.ItemDeUsuarioDto
import com.example.mixtapp.data.dtos.MensajeDto
import com.example.mixtapp.data.dtos.UsuarioDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// La comunidad en Firestore:
// - foros/{itemId} con su subcoleccion mensajes
// - chats/{chatId} con su subcoleccion mensajes; participantes guarda los dos uid para buscar mis chats
class ComunidadFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore,
) : ComunidadRemoteDataSource {

    override suspend fun getUsuarios(limite: Int): List<UsuarioPerfilFirestoreDto> {
        val snapshot = db.collection("usuarios").limit(limite.toLong()).get().await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(UsuarioPerfilFirestoreDto::class.java)?.copy(id = doc.id)
        }
    }

    override suspend fun getLikes(usuarioId: String): List<ItemDeUsuarioDto> {
        val snapshot = db.collection("usuarios").document(usuarioId).collection("likes").get().await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(ItemDeUsuarioDto::class.java)?.copy(id = doc.id)
        }
    }

    override fun listenForos(limite: Int): Flow<List<ForoDto>> = callbackFlow {
        val listenerRegistration = db.collection("foros")
            .orderBy("actualizadoEn", Query.Direction.DESCENDING)
            .limit(limite.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val foros = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ForoDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(foros).isSuccess // envia la lista al flujo
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun listenMensajesDelForo(itemId: String): Flow<List<MensajeDto>> =
        listenMensajes(db.collection("foros").document(itemId).collection("mensajes"))

    // En un batch: el mensaje nuevo, la copia del item y el contador del foro. merge deja lo demas
    override suspend fun publicarEnForo(itemId: String, item: CopiaDeItemDto, mensaje: CreateMensajeDto) {
        val foroRef = db.collection("foros").document(itemId)
        val mensajeRef = foroRef.collection("mensajes").document()

        db.batch()
            .set(mensajeRef, mensaje)
            .set(
                foroRef,
                mapOf(
                    "item" to item,
                    "mensajesCount" to FieldValue.increment(1),
                    "actualizadoEn" to mensaje.createdAt,
                ),
                SetOptions.merge(),
            )
            .commit()
            .await()
    }

    // Sin orderBy en la consulta: con array-contains pediria un indice compuesto. El repositorio
    // ordena por la fecha
    override fun listenChats(usuarioId: String): Flow<List<ChatDto>> = callbackFlow {
        val listenerRegistration = db.collection("chats")
            .whereArrayContains("participantes", usuarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val chats = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChatDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(chats).isSuccess // envia la lista al flujo
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun listenMensajesDelChat(chatId: String): Flow<List<MensajeDto>> =
        listenMensajes(db.collection("chats").document(chatId).collection("mensajes"))

    // En un batch: el mensaje nuevo y el documento del chat con el ultimo mensaje
    override suspend fun enviarMensaje(chatId: String, participantes: List<UsuarioDto>, mensaje: CreateMensajeDto) {
        val chatRef = db.collection("chats").document(chatId)
        val mensajeRef = chatRef.collection("mensajes").document()

        db.batch()
            .set(mensajeRef, mensaje)
            .set(
                chatRef,
                mapOf(
                    "participantes" to participantes.map { it.id },
                    "usuarios" to participantes,
                    "ultimoMensaje" to mensaje.texto,
                    "actualizadoEn" to mensaje.createdAt,
                ),
                SetOptions.merge(),
            )
            .commit()
            .await()
    }

    // Los mensajes de un foro o de un chat, del mas viejo al mas nuevo, como un chat
    private fun listenMensajes(coleccion: CollectionReference): Flow<List<MensajeDto>> =
        callbackFlow {
            val listenerRegistration = coleccion
                .orderBy("createdAt")
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        close(error) // cierra el flujo con error
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val mensajes = snapshot.documents.mapNotNull { doc ->
                            doc.toObject(MensajeDto::class.java)?.copy(id = doc.id)
                        }
                        trySend(mensajes).isSuccess // envia la lista al flujo
                    }
                }

            awaitClose { listenerRegistration.remove() }
        }
}
