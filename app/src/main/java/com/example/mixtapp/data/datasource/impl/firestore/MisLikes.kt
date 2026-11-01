package com.example.mixtapp.data.datasource.impl.firestore

import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.tasks.await

// Si en la subcoleccion likes del documento hay uno con mi uid, ya le di like. Sin usuario no se
// consulta: Retrofit y las pantallas que no pintan corazones no lo necesitan
suspend fun DocumentReference.tieneMiLike(currentUserId: String): Boolean {
    if (currentUserId.isEmpty()) return false

    return collection("likes").document(currentUserId).get().await().exists()
}
