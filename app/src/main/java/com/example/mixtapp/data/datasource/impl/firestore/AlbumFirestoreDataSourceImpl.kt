package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AlbumFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : AlbumRemoteDataSource {

    override suspend fun getAlbumes(): List<AlbumDto> {
        val snapshot = db.collection("albumes").get().await()

        // El id no viene dentro de los campos: se copia el del documento para poder navegar
        return snapshot.documents.map { doc ->
            val album = doc.toObject(AlbumDto::class.java)
            album?.copy(id = doc.id) ?: throw Exception("Album not found")
        }
    }

    override suspend fun getAlbumById(id: String): AlbumDto {
        val respuesta = db.collection("albumes").document(id).get().await()
        val album = respuesta.toObject(AlbumDto::class.java)

        // Si el documento no existe, toObject da null: el repositorio lo muestra como no encontrado
        return album?.copy(id = respuesta.id) ?: throw NoSuchElementException("No existe el album $id")
    }

    override suspend fun getReviewsDeAlbum(id: String, currentUserId: String): List<ReviewConUsuarioDto> {
        val snapshot = db.collection("reviews").whereEqualTo("albumId", id).get().await()

        return snapshot.documents.map { doc ->
            val review = doc.toObject(ReviewConUsuarioDto::class.java)?.copy(id = doc.id)
                ?: throw Exception("Review not found")

            // Si en la subcoleccion likes hay un documento con mi uid, ya le di like
            if (currentUserId.isNotEmpty()) {
                val likeSnapshot = doc.reference.collection("likes").document(currentUserId).get().await()
                review.liked = likeSnapshot.exists()
            }

            review
        }
    }

    // El listener escucha la coleccion reviews: cada cambio manda la lista completa por el flujo.
    // Un like solo cambia likesCount en la resena, asi que tambien llega por aqui
    override fun listenAllReviews(currentUserId: String): Flow<List<ReviewConUsuarioDto>> = callbackFlow {
        val listenerRegistration = db.collection("reviews")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reviews = snapshot.documents.map { doc ->
                        val review = doc.toObject(ReviewConUsuarioDto::class.java)
                        review?.copy(id = doc.id) ?: throw Exception("Review not found")
                    }
                    trySend(reviews).isSuccess // envia la lista al flujo
                }
            }

        // Cuando nadie escucha el flujo, se quita el listener para no dejar la conexion activa
        awaitClose { listenerRegistration.remove() }
    }
}
