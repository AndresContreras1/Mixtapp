package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ReviewRemoteDataSource {

    // La transaccion conserva los cambios de otros usuarios y no duplica la relacion.
    suspend fun actualizarLike(id: String, usuarioId: String, activo: Boolean): List<String> {
        val referencia = db.collection("reviews").document(id)
        return db.runTransaction { transaccion ->
            val documento = transaccion.get(referencia)
            if (!documento.exists()) throw NoSuchElementException("No existe el documento $id")
            val actuales = (documento.get("likesDeUsuarios") as? List<*>)
                ?.filterIsInstance<String>() ?: emptyList()
            val nuevos = if (activo) (actuales + usuarioId).distinct() else actuales.filter { it != usuarioId }
            transaccion.update(referencia, "likesDeUsuarios", nuevos)
            nuevos
        }.await()
    }

    // add crea el documento con un id aleatorio
    override suspend fun createReview(review: CreateReviewDto) {
        db.collection("reviews").add(review).await()
    }

    // Se actualiza el contenido sin borrar los likes
    override suspend fun updateReview(id: String, review: CreateReviewDto) {
        db.collection("reviews").document(id).set(review, SetOptions.merge()).await()
    }

    override suspend fun deleteReview(id: String): Unit {
        db.collection("reviews").document(id).delete().await()
    }
}
