package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.dtos.CopiaDeItemDto
import com.example.mixtapp.data.dtos.ReviewDto
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

// Si en la subcoleccion likes del documento hay uno con mi uid, ya le di like. Sin usuario no se
// consulta: Retrofit y las pantallas que no pintan corazones no lo necesitan
suspend fun DocumentReference.tieneMiLike(currentUserId: String): Boolean {
    if (currentUserId.isEmpty()) return false

    return collection("likes").document(currentUserId).get().await().exists()
}

// Mi like de cada resena se pide a la vez y se espera el mas lento, en vez de uno por uno
suspend fun FirebaseFirestore.conMisLikes(reviews: List<ReviewDto>, currentUserId: String): List<ReviewDto> =
    coroutineScope {
        val pendientes = reviews.map { review ->
            async {
                review.liked = collection("reviews").document(review.id).tieneMiLike(currentUserId)
                review
            }
        }

        pendientes.map { it.await() }
    }

// El like a un album, una cancion o un artista son tres escrituras en una transaccion, como seguir
// a alguien: el like en el item, la copia en mis likes y el contador del item. Si alguna falla,
// Firebase deshace las demas
suspend fun FirebaseFirestore.sendOrDeleteLikeDeItem(
    coleccion: String,
    itemId: String,
    userId: String,
    copia: CopiaDeItemDto,
) {
    val itemRef = collection(coleccion).document(itemId)
    val likeRef = itemRef.collection("likes").document(userId)
    val miLikeRef = collection("usuarios").document(userId).collection("likes").document(itemId)

    runTransaction { transaction ->
        val likeDoc = transaction.get(likeRef)

        if (likeDoc.exists()) {
            transaction.delete(likeRef)
            transaction.delete(miLikeRef)
            transaction.update(itemRef, "likesCount", FieldValue.increment(-1))
        } else {
            transaction.set(likeRef, mapOf("timestamp" to FieldValue.serverTimestamp()))
            transaction.set(miLikeRef, copia)
            transaction.update(itemRef, "likesCount", FieldValue.increment(1))
        }
    }.await()
}
