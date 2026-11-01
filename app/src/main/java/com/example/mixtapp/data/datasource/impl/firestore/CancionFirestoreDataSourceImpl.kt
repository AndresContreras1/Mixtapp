package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.CancionRemoteDataSource
import com.example.mixtapp.data.dtos.CancionDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Igual que los albumes, con la coleccion canciones
class CancionFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : CancionRemoteDataSource {

    override suspend fun getCanciones(): List<CancionDto> {
        val snapshot = db.collection("canciones").get().await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(CancionDto::class.java)?.copy(id = doc.id)
        }
    }

    override suspend fun getCancionById(id: String, currentUserId: String): CancionDto? {
        val cancionRef = db.collection("canciones").document(id)
        val respuesta = cancionRef.get().await()
        // Si el documento no existe se retorna null: el repositorio decide el error
        val cancion = respuesta.toObject(CancionDto::class.java)?.copy(id = respuesta.id) ?: return null

        cancion.liked = cancionRef.tieneMiLike(currentUserId)
        return cancion
    }

    override fun listenCanciones(): Flow<List<CancionDto>> = callbackFlow {
        val listenerRegistration = db.collection("canciones")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val canciones = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(CancionDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(canciones).isSuccess // envia la lista al flujo
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun listenCancionById(id: String, currentUserId: String): Flow<CancionDto?> = callbackFlow {
        val listenerRegistration = db.collection("canciones").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val cancion = snapshot.toObject(CancionDto::class.java)?.copy(id = snapshot.id)
                    trySend(cancion).isSuccess // envia la cancion (o null) al flujo
                }
            }

        awaitClose { listenerRegistration.remove() }
    }.map { cancion ->
        if (cancion != null) {
            cancion.liked = db.collection("canciones").document(cancion.id).tieneMiLike(currentUserId)
        }

        cancion
    }

    override suspend fun sendOrDeleteLike(cancionId: String, userId: String, copia: CopiaDeItemDto) {
        db.sendOrDeleteLikeDeItem(coleccion = "canciones", itemId = cancionId, userId = userId, copia = copia)
    }
}
