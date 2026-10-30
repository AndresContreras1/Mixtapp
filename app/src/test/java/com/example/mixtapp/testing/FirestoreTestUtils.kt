package com.example.mixtapp.testing

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// La app ya apunta al emulador (BaseApplication), asi que la instancia por defecto es la del emulador
fun db(): FirebaseFirestore = FirebaseFirestore.getInstance()

suspend fun crearAlbum(
    id: String,
    titulo: String = "Album de prueba",
    artista: String = "Artista de prueba",
    anio: Int = 2024
) {
    db().collection("albumes").document(id).set(
        mapOf("titulo" to titulo, "artista" to artista, "portadaUrl" to "", "anio" to anio, "genero" to "Rock")
    ).await()
}

suspend fun crearUsuario(
    id: String,
    nombre: String = "Usuario de prueba",
    followersCount: Int = 0,
    followingCount: Int = 0
) {
    db().collection("usuarios").document(id).set(
        mapOf(
            "username" to "user_$id",
            "nombre" to nombre,
            "bio" to "Bio de prueba",
            "fotoUrl" to null,
            "followersCount" to followersCount,
            "followingCount" to followingCount,
            "fcmtoken" to ""
        )
    ).await()
}

suspend fun crearResena(
    id: String,
    usuarioId: String,
    albumId: String,
    calificacion: Int = 5,
    likesCount: Int = 0,
    nombreUsuario: String = "Usuario de prueba"
) {
    db().collection("reviews").document(id).set(
        mapOf(
            "usuarioId" to usuarioId,
            "albumId" to albumId,
            "calificacion" to calificacion,
            "comentario" to "Comentario de prueba",
            "fechaEscucha" to "2026-10-01",
            "likesCount" to likesCount,
            "usuario" to mapOf("nombre" to nombreUsuario, "fotoUrl" to null),
            "album" to mapOf("titulo" to "Album de prueba", "artista" to "Artista de prueba", "portadaUrl" to "")
        )
    ).await()
}

suspend fun darLike(reviewId: String, usuarioId: String) {
    db().collection("reviews").document(reviewId).collection("likes").document(usuarioId)
        .set(mapOf("timestamp" to System.currentTimeMillis())).await()
}

suspend fun leer(coleccion: String, id: String): DocumentSnapshot =
    db().collection(coleccion).document(id).get().await()

// Firestore no borra las subcolecciones al borrar el documento: se borran una por una
suspend fun borrarDocumento(coleccion: String, id: String, vararg subcolecciones: String) {
    val ref = db().collection(coleccion).document(id)
    subcolecciones.forEach { sub ->
        ref.collection(sub).get().await().documents.forEach { it.reference.delete().await() }
    }
    ref.delete().await()
}

// Para las resenas que crea el codigo bajo prueba con id aleatorio
suspend fun borrarResenasDe(usuarioId: String) {
    db().collection("reviews").whereEqualTo("usuarioId", usuarioId).get().await().documents
        .forEach { doc -> borrarDocumento("reviews", doc.id, "likes") }
}
