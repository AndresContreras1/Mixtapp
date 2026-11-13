package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.SonandoRemoteDataSource
import com.example.mixtapp.data.dtos.CreateReaccionDto
import com.example.mixtapp.data.dtos.CreateSonandoDto
import com.example.mixtapp.data.dtos.SonandoDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Cuantos trae la fila: los mas recientes. El repositorio deja solo los de quienes sigo
private const val MAX_SONANDO = 50

class SonandoFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : SonandoRemoteDataSource {

    // El listener escucha la coleccion sonando: cuando alguien pone otra cancion o le llega una
    // reaccion, la lista completa llega por el flujo. Mi reaccion no esta en el documento, asi que
    // se busca en la subcoleccion cada vez que llega
    override fun listenSonando(currentUserId: String): Flow<List<SonandoDto>> = callbackFlow {
        val listenerRegistration = db.collection("sonando")
            .orderBy("actualizadoEn", Query.Direction.DESCENDING)
            .limit(MAX_SONANDO.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    // El id del documento es el uid de quien escucha
                    val sonando = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(SonandoDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(sonando).isSuccess // envia la lista al flujo
                }
            }

        // Cuando se sale de la pantalla nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }.map { sonando -> conMisReacciones(sonando = sonando, currentUserId = currentUserId) }

    // Las reacciones a lo que sonaba antes se borran, para que no cuenten si vuelve a sonar lo
    // mismo; despues se guarda lo nuevo con el contador en 0
    override suspend fun ponerSonando(usuarioId: String, sonando: CreateSonandoDto) {
        val sonandoRef = db.collection("sonando").document(usuarioId)

        val reacciones = sonandoRef.collection("reacciones").get().await()
        for (reaccion in reacciones) reaccion.reference.delete().await()

        sonandoRef.set(sonando).await()
    }

    // Borrar el documento no borra sus subcolecciones: primero se borran las reacciones
    override suspend fun quitarSonando(usuarioId: String) {
        val sonandoRef = db.collection("sonando").document(usuarioId)

        val reacciones = sonandoRef.collection("reacciones").get().await()
        for (reaccion in reacciones) reaccion.reference.delete().await()

        sonandoRef.delete().await()
    }

    // Igual que un like: la reaccion y el contador van en una transaccion, o se hacen las dos o
    // ninguna
    override suspend fun reaccionar(usuarioId: String, reaccion: CreateReaccionDto) {
        val sonandoRef = db.collection("sonando").document(usuarioId)
        val reaccionRef = sonandoRef.collection("reacciones")
            .document(idDeReaccion(itemId = reaccion.itemId, reactorId = reaccion.usuarioId))

        db.runTransaction { transaction ->
            val reaccionDoc = transaction.get(reaccionRef)

            if (reaccionDoc.exists()) {
                transaction.delete(reaccionRef)
                transaction.update(sonandoRef, "reaccionesCount", FieldValue.increment(-1))
            } else {
                transaction.set(reaccionRef, reaccion)
                transaction.update(sonandoRef, "reaccionesCount", FieldValue.increment(1))
            }
        }.await()
    }

    // Mi reaccion de cada uno se pide a la vez y se espera la mas lenta, como los likes
    private suspend fun conMisReacciones(sonando: List<SonandoDto>, currentUserId: String): List<SonandoDto> =
        coroutineScope {
            if (currentUserId.isEmpty()) return@coroutineScope sonando

            val pendientes = sonando.map { dto ->
                async {
                    dto.reaccionado = db.collection("sonando").document(dto.id)
                        .collection("reacciones")
                        .document(idDeReaccion(itemId = dto.itemId, reactorId = currentUserId))
                        .get().await().exists()
                    dto
                }
            }

            pendientes.map { it.await() }
        }

    private fun idDeReaccion(itemId: String, reactorId: String): String = "${itemId}_$reactorId"
}
