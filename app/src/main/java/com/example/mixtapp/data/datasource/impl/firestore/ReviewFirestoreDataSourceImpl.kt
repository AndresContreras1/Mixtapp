package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.dtos.ComentarioDto
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ReviewRemoteDataSource {

    override suspend fun getReviewById(id: String, currentUserId: String): ReviewConAlbumDto? {
        val reviewRef = db.collection("reviews").document(id)
        val respuesta = reviewRef.get().await()
        // Si el documento no existe se retorna null: el repositorio decide el error
        val review = respuesta.toObject(ReviewConAlbumDto::class.java)?.copy(id = respuesta.id)
            ?: return null

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

    // Borrar el documento no borra sus subcolecciones: los likes y los comentarios (con los likes de
    // cada comentario) se eliminan uno por uno
    override suspend fun deleteReview(id: String) {
        val reviewRef = db.collection("reviews").document(id)
        val likesSnapshot = reviewRef.collection("likes").get().await()

        for (doc in likesSnapshot.documents) {
            doc.reference.delete().await()
        }

        val comentariosSnapshot = reviewRef.collection("comments").get().await()

        for (comentario in comentariosSnapshot.documents) {
            val likesComentario = comentario.reference.collection("likes").get().await()
            for (like in likesComentario.documents) like.reference.delete().await()

            comentario.reference.delete().await()
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

    // El listener escucha el documento de la resena. Mi like no esta en el documento, asi que se
    // busca con exists() cada vez que llega
    override fun listenReviewById(id: String, currentUserId: String): Flow<ReviewConAlbumDto> = callbackFlow {
        val listenerRegistration = db.collection("reviews").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    // Si la borran mientras se ve, toObject da null: el repositorio lo muestra como no encontrada
                    val review = snapshot.toObject(ReviewConAlbumDto::class.java)?.copy(id = snapshot.id)

                    if (review == null) close(NoSuchElementException("No existe la resena $id"))
                    else trySend(review).isSuccess // envia la resena al flujo
                }
            }

        // Cuando se sale de la discusion nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }.map { review ->
        if (currentUserId.isNotEmpty()) {
            val likeSnapshot = db.collection("reviews").document(review.id)
                .collection("likes").document(currentUserId).get().await()
            review.liked = likeSnapshot.exists()
        }

        review
    }

    // El comentario y el contador van en una transaccion: si uno falla, Firebase deshace el otro.
    // document() sin id crea la referencia con un id aleatorio, como add
    override suspend fun createComentario(reviewId: String, comentario: CreateComentarioDto) {
        val reviewRef = db.collection("reviews").document(reviewId)
        val comentarioRef = reviewRef.collection("comments").document()

        db.runTransaction { transaction ->
            transaction.set(comentarioRef, comentario)
            transaction.update(reviewRef, "commentsCount", FieldValue.increment(1))
        }.await()
    }

    // Igual que el like de la resena: reviews/{id}/comments/{comentario}/likes/{mi uid}
    override suspend fun sendOrDeleteLikeComentario(reviewId: String, comentarioId: String, userId: String) {
        val comentarioRef = db.collection("reviews").document(reviewId)
            .collection("comments").document(comentarioId)
        val likeRef = comentarioRef.collection("likes").document(userId)

        db.runTransaction { transaction ->
            val likeDoc = transaction.get(likeRef)

            if (likeDoc.exists()) {
                transaction.delete(likeRef)
                transaction.update(comentarioRef, "likesCount", FieldValue.increment(-1))
            } else {
                transaction.set(likeRef, mapOf("timestamp" to FieldValue.serverTimestamp()))
                transaction.update(comentarioRef, "likesCount", FieldValue.increment(1))
            }
        }.await()
    }

    // El listener escucha la subcoleccion de comentarios, del mas viejo al mas nuevo. Un like solo
    // cambia likesCount del comentario, asi que tambien llega por aqui
    override fun listenComentarios(reviewId: String, currentUserId: String): Flow<List<ComentarioDto>> = callbackFlow {
        val listenerRegistration = db.collection("reviews").document(reviewId).collection("comments")
            .orderBy("createdAt")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val comentarios = snapshot.documents.map { doc ->
                        val comentario = doc.toObject(ComentarioDto::class.java)
                        comentario?.copy(id = doc.id) ?: throw Exception("Comment not found")
                    }
                    trySend(comentarios).isSuccess // envia la lista al flujo
                }
            }

        // Cuando se sale de la discusion nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }.map { comentarios ->
        comentarios.map { comentario ->
            if (currentUserId.isNotEmpty()) {
                val likeSnapshot = db.collection("reviews").document(reviewId)
                    .collection("comments").document(comentario.id)
                    .collection("likes").document(currentUserId).get().await()
                comentario.liked = likeSnapshot.exists()
            }

            comentario
        }
    }
}
