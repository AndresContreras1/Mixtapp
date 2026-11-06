package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.DueloRemoteDataSource
import com.example.mixtapp.data.dtos.CreateDueloDto
import com.example.mixtapp.data.dtos.CreateVotoDto
import com.example.mixtapp.data.dtos.DueloDto
import com.example.mixtapp.data.dtos.VotoDto
import com.example.mixtapp.data.model.OPCION_A
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Cuantas opiniones trae la pantalla: las mas nuevas
private const val MAX_OPINIONES = 50

class DueloFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : DueloRemoteDataSource {

    // El listener escucha el documento del duelo: cada voto cambia los contadores y llega solo. Si
    // nadie ha votado el documento no existe, toObject da null y se envia null
    override fun listenDuelo(dueloId: String): Flow<DueloDto?> = callbackFlow {
        val listenerRegistration = db.collection("duelos").document(dueloId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val duelo = snapshot.toObject(DueloDto::class.java)?.copy(id = snapshot.id)
                    trySend(duelo).isSuccess // envia el duelo (o null) al flujo
                }
            }

        // Cuando se sale de la pantalla nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }

    // El listener escucha la subcoleccion de votos: una opinion nueva llega a todos sin recargar
    override fun listenVotos(dueloId: String): Flow<List<VotoDto>> = callbackFlow {
        val listenerRegistration = db.collection("duelos").document(dueloId).collection("votos")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(MAX_OPINIONES.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    // El id del voto es el uid de quien voto
                    val votos = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(VotoDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(votos).isSuccess // envia la lista al flujo
                }
            }

        // Cuando se sale de la pantalla nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }

    // Todo en una transaccion: el voto y los contadores se guardan juntos o ninguno. En una
    // transaccion primero van todas las lecturas y despues las escrituras
    override suspend fun votar(dueloId: String, duelo: CreateDueloDto, usuarioId: String, voto: CreateVotoDto) {
        val dueloRef = db.collection("duelos").document(dueloId)
        val votoRef = dueloRef.collection("votos").document(usuarioId)

        db.runTransaction { transaction ->
            val dueloDoc = transaction.get(dueloRef)
            // Por cual habia votado antes; null si es mi primer voto en este duelo
            val opcionAnterior = transaction.get(votoRef).getString("opcion")

            if (!dueloDoc.exists()) {
                // El primer voto del dia crea el duelo con ese voto ya contado
                transaction.set(dueloRef, duelo)
            } else if (opcionAnterior != voto.opcion) {
                transaction.update(dueloRef, campoDeVotos(voto.opcion), FieldValue.increment(1))
                if (opcionAnterior != null) {
                    // Cambio de opinion: el voto pasa del otro a este
                    transaction.update(dueloRef, campoDeVotos(opcionAnterior), FieldValue.increment(-1))
                }
            }

            // Si ya habia votado por el mismo, solo cambia la opinion
            transaction.set(votoRef, voto)
        }.await()
    }

    private fun campoDeVotos(opcion: String): String = if (opcion == OPCION_A) "votosA" else "votosB"
}
