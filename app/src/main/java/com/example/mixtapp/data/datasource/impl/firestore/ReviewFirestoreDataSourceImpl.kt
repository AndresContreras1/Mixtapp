package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ReviewRemoteDataSource {

    override suspend fun getReviewById(id: String, currentUserId: String): ReviewConAlbumDto {
        val reviewRef = db.collection("reviews").document(id)
        val respuesta = reviewRef.get().await()
        val review = respuesta.toObject(ReviewConAlbumDto::class.java)?.copy(id = respuesta.id)
            ?: throw NoSuchElementException("No existe la resena $id")

        if (currentUserId.isNotEmpty()) {
            val likeSnapshot = reviewRef.collection("likes").document(currentUserId).get().await()
            review.liked = likeSnapshot.exists()
        }

        return review
    }

    // add crea el documento con un id aleatorio
    override suspend fun createReview(review: CreateReviewDto) {
        db.collection("reviews").add(review).await()
    }

    // Solo se cambia lo que se edita y la copia de quien la escribe: el contador de likes y la
    // subcoleccion se conservan
    override suspend fun updateReview(id: String, review: CreateReviewDto) {
        db.collection("reviews").document(id).update(
            mapOf(
                "calificacion" to review.calificacion,
                "comentario" to review.comentario,
                "fechaEscucha" to review.fechaEscucha,
                "usuario.nombre" to review.usuario?.nombre,
                "usuario.fotoUrl" to review.usuario?.fotoUrl,
            )
        ).await()
    }

    // Borrar el documento no borra su subcoleccion: los likes se eliminan uno por uno
    override suspend fun deleteReview(id: String) {
        val reviewRef = db.collection("reviews").document(id)
        val likesSnapshot = reviewRef.collection("likes").get().await()

        for (doc in likesSnapshot.documents) {
            doc.reference.delete().await()
        }

        reviewRef.delete().await()
    }

    // El like y el contador van en una transaccion: si uno falla, Firebase deshace el otro
    override suspend fun sendOrDeleteLike(reviewId: String, userId: String) {
        val reviewRef = db.collection("reviews").document(reviewId)
        val likeRef = reviewRef.collection("likes").document(userId)

        db.runTransaction { transaction ->
            val likeDoc = transaction.get(likeRef)

            if (likeDoc.exists()) {
                transaction.delete(likeRef)
                transaction.update(reviewRef, "likesCount", FieldValue.increment(-1))
            } else {
                transaction.set(likeRef, mapOf("timestamp" to FieldValue.serverTimestamp()))
                transaction.update(reviewRef, "likesCount", FieldValue.increment(1))
            }
        }.await()
    }
}
