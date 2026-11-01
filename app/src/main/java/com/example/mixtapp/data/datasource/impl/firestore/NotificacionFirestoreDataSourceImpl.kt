package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.NotificacionRemoteDataSource
import com.example.mixtapp.data.dtos.NotificacionDto
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Cuantas notificaciones trae la pantalla: las mas nuevas
private const val MAX_NOTIFICACIONES = 50

class NotificacionFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : NotificacionRemoteDataSource {

    // El listener escucha mi subcoleccion de notificaciones: cuando la Cloud Function guarda una
    // nueva, llega la lista completa por el flujo
    override fun listenNotificaciones(usuarioId: String): Flow<List<NotificacionDto>> = callbackFlow {
        val listenerRegistration = db.collection("usuarios").document(usuarioId).collection("notificaciones")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .limit(MAX_NOTIFICACIONES.toLong())
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notificaciones = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(NotificacionDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(notificaciones).isSuccess // envia la lista al flujo
                }
            }

        // Cuando se sale de la pantalla nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }

    // update solo cambia leida; la marca llega de vuelta por el listener
    override suspend fun marcarLeida(usuarioId: String, notificacionId: String) {
        db.collection("usuarios").document(usuarioId)
            .collection("notificaciones").document(notificacionId)
            .update("leida", true)
            .await()
    }
}
