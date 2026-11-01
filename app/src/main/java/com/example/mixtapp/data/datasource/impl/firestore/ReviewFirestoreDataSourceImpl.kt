package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.dtos.ComentarioDto
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
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

        review.liked = reviewRef.tieneMiLike(currentUserId)
        return review
    }

    // add crea el documento con un id aleatorio
    override suspend fun createReview(review: CreateReviewDto) {
        db.collection("reviews").add(review).await()
    }

    // Solo se cambia lo que se edita, la fecha de la edicion y la copia de quien la escribe: el
    // contador de likes y la subcoleccion se conservan
    override suspend fun updateReview(id: String, review: CreateReviewDto) {
        db.collection("reviews").document(id).update(
            mapOf(
                "calificacion" to review.calificacion,
                "comentario" to review.comentario,
                "fechaEscucha" to review.fechaEscucha,
                "updatedAt" to review.updatedAt,
                "usuario.nombre" to review.usuario?.nombre,
                "usuario.fotoUrl" to review.usuario?.fotoUrl,
            )
        ).await()
    }

    // Borrar el documento no borra sus subcolecciones: los likes y los comentarios (con los likes
    // de cada comentario) se borran en un solo batch junto con la resena. Un batch maneja hasta 500
    // operaciones, muchas mas de las que tiene una resena
    override suspend fun deleteReview(id: String) {
        val reviewRef = db.collection("reviews").document(id)
        val batch = db.batch()

        val likesSnapshot = reviewRef.collection("likes").get().await()
        for (like in likesSnapshot.documents) batch.delete(like.reference)

        val comentariosSnapshot = reviewRef.collection("comments").get().await()
        for (comentario in comentariosSnapshot.documents) {
            val likesComentario = comentario.reference.collection("likes").get().await()
            for (like in likesComentario.documents) batch.delete(like.reference)

            batch.delete(comentario.reference)
        }

        batch.delete(reviewRef)
        batch.commit().await()
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

    // El listener escucha el documento de la resena. Si la borran mientras se ve, toObject da null
    // y se envia null: el repositorio decide el error. Mi like no esta en el documento, asi que se
    // busca con exists() cada vez que llega
    override fun listenReviewById(id: String, currentUserId: String): Flow<ReviewConAlbumDto?> = callbackFlow {
        val listenerRegistration = db.collection("reviews").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val review = snapshot.toObject(ReviewConAlbumDto::class.java)?.copy(id = snapshot.id)
                    trySend(review).isSuccess // envia la resena (o null) al flujo
                }
            }

        // Cuando se sale de la discusion nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }.map { review ->
        if (review != null) {
            review.liked = db.collection("reviews").document(review.id).tieneMiLike(currentUserId)
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
                    val comentarios = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ComentarioDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(comentarios).isSuccess // envia la lista al flujo
                }
            }

        // Cuando se sale de la discusion nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }.map { comentarios ->
        // Mi like de cada comentario se pide a la vez y se espera el mas lento
        coroutineScope {
            val pendientes = comentarios.map { comentario ->
                async {
                    comentario.liked = db.collection("reviews").document(reviewId)
                        .collection("comments").document(comentario.id)
                        .tieneMiLike(currentUserId)
                    comentario
                }
            }

            pendientes.map { it.await() }
        }
    }
}
