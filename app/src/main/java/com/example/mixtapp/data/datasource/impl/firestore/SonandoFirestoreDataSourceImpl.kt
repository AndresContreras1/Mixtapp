package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.SonandoRemoteDataSource
import com.example.mixtapp.data.dtos.CreateReaccionDto
import com.example.mixtapp.data.dtos.CreateSonandoDto
import com.example.mixtapp.data.dtos.SonandoDto
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.WriteBatch
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SonandoFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : SonandoRemoteDataSource {

    // El listener escucha lo que se puso a sonar desde esa fecha: cuando alguien pone otra cancion
    // o le llega una reaccion, la lista llega por el flujo. actualizadoEn va en formato ISO, asi que
    // comparar el texto compara las fechas, como en el mapa. Mi reaccion no esta en el documento,
    // asi que se busca en la subcoleccion cada vez que llega
    override fun listenSonando(desde: String, currentUserId: String): Flow<List<SonandoDto>> = callbackFlow {
        val listenerRegistration = db.collection("sonando")
            .whereGreaterThanOrEqualTo("actualizadoEn", desde)
            .orderBy("actualizadoEn", Query.Direction.DESCENDING)
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
    // mismo, y se guarda lo nuevo con el contador en 0. Todo va en un batch: o se escribe todo o
    // nada, asi el contador nunca queda distinto de las reacciones que hay
    override suspend fun ponerSonando(usuarioId: String, sonando: CreateSonandoDto) {
        val sonandoRef = db.collection("sonando").document(usuarioId)
        val batch = db.batch()

        borrarReacciones(sonandoRef = sonandoRef, batch = batch)
        batch.set(sonandoRef, sonando)

        batch.commit().await()
    }

    // Borrar el documento no borra sus subcolecciones: las reacciones se borran en el mismo batch
    override suspend fun quitarSonando(usuarioId: String) {
        val sonandoRef = db.collection("sonando").document(usuarioId)
        val batch = db.batch()

        borrarReacciones(sonandoRef = sonandoRef, batch = batch)
        batch.delete(sonandoRef)

        batch.commit().await()
    }

    // Lee las reacciones de lo que suena y agrega su borrado al batch. Son pocas: una por cada
    // persona que reacciono, muy lejos del limite de 500 del batch
    private suspend fun borrarReacciones(sonandoRef: DocumentReference, batch: WriteBatch) {
        val reacciones = sonandoRef.collection("reacciones").get().await()

        for (reaccion in reacciones) batch.delete(reaccion.reference)
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
