package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.dtos.CreateMixtapeDto
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class MixtapeFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : MixtapeRemoteDataSource {

    // El listener escucha las mixtapes de un usuario con whereEqualTo. Se ordenan aqui por la fecha
    // (texto ISO) para no pedir un indice compuesto en Firestore
    override fun listenMixtapesDeUsuario(usuarioId: String): Flow<List<MixtapeDto>> = callbackFlow {
        val listenerRegistration = db.collection("mixtapes")
            .whereEqualTo("usuarioId", usuarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val mixtapes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MixtapeDto::class.java)?.copy(id = doc.id)
                    }.sortedByDescending { it.createdAt }
                    trySend(mixtapes).isSuccess // envia la lista al flujo
                }
            }

        // Cuando se sale de la pantalla nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }

    // Si la borran mientras se ve, toObject da null y se envia null: el repositorio decide el error
    override fun listenMixtapeById(id: String): Flow<MixtapeDto?> = callbackFlow {
        val listenerRegistration = db.collection("mixtapes").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val mixtape = snapshot.toObject(MixtapeDto::class.java)?.copy(id = snapshot.id)
                    trySend(mixtape).isSuccess // envia la mixtape (o null) al flujo
                }
            }

        // Cuando se sale del detalle nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }

    // add crea el documento con un id aleatorio, como un comentario
    override suspend fun crearMixtape(mixtape: CreateMixtapeDto): String {
        val documento = db.collection("mixtapes").add(mixtape).await()

        return documento.id
    }

    // En una transaccion se lee la lista actual y se escribe con el item nuevo: si dos celulares
    // agregan a la vez, ninguno borra lo del otro
    override suspend fun agregarItem(mixtapeId: String, item: ItemMixtapeDto) {
        val mixtapeRef = db.collection("mixtapes").document(mixtapeId)

        db.runTransaction { transaction ->
            val mixtape = transaction.get(mixtapeRef).toObject(MixtapeDto::class.java)

            // Si la borraron o el item ya esta, no se escribe nada
            if (mixtape != null && mixtape.items.none { it.id == item.id }) {
                transaction.update(mixtapeRef, "items", mixtape.items + item)
            }
        }.await()
    }

    override suspend fun quitarItem(mixtapeId: String, itemId: String) {
        val mixtapeRef = db.collection("mixtapes").document(mixtapeId)

        db.runTransaction { transaction ->
            val mixtape = transaction.get(mixtapeRef).toObject(MixtapeDto::class.java)

            if (mixtape != null) {
                transaction.update(mixtapeRef, "items", mixtape.items.filterNot { it.id == itemId })
            }
        }.await()
    }

    // La mixtape no tiene subcolecciones: basta con borrar el documento
    override suspend fun eliminarMixtape(mixtapeId: String) {
        db.collection("mixtapes").document(mixtapeId).delete().await()
    }
}
