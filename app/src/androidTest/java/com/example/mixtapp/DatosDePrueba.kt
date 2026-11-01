package com.example.mixtapp

import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.CreateReviewItemDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.HistoriaDto
import com.example.mixtapp.data.dtos.SeccionDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

// Los datos los crea la propia prueba, no la carga con Faker: asi cada prueba es replicable.
// Estan en un solo archivo para no copiarlos en cada clase de prueba

// Todas las pruebas usan el emulador de Firebase. Cada useEmulator va en su propio try: si otra prueba
// de la misma corrida ya lo conecto, lanza una excepcion que no importa, y asi no deja al otro sin
// conectar
fun conectarEmulador() {
    try {
        Firebase.firestore.useEmulator("10.0.2.2", 8080)
    } catch (e: Exception) {

    }
    try {
        Firebase.auth.useEmulator("10.0.2.2", 9099)
    } catch (e: Exception) {

    }
}

fun generateUsuario(i: Int): UsuarioPerfilFirestoreDto = UsuarioPerfilFirestoreDto(
    id = "usuario_$i",
    username = "username_$i",
    nombre = "Nombre $i",
    bio = "Bio del usuario $i",
    fotoUrl = "https://picsum.photos/200/200?random=$i",
    followersCount = i * 10,
    followingCount = i * 5,
    fcmtoken = null,
)

fun generateAlbum(i: Int): AlbumDto = AlbumDto(
    id = "album_$i",
    titulo = "Album $i",
    artista = "Artista $i",
    portadaUrl = "https://picsum.photos/600/600?random=$i",
    anio = 2000 + i,
    fechaLanzamiento = "${2000 + i}-01-01",
    genero = "Rock",
    subgenero = "Rock alternativo",
    tendencia = null,
    likesCount = 0,
    createdAt = "2025-01-01T00:00:00Z",
    updatedAt = "2025-01-01T00:00:00Z",
)

// Los datos curiosos del album i: dos secciones, como las que carga el seed
fun generateHistoria(i: Int): HistoriaDto = HistoriaDto(
    tipo = TIPO_ALBUM,
    secciones = listOf(
        SeccionDto(titulo = "Historia", texto = "Historia del album $i"),
        SeccionDto(titulo = "Dato curioso", texto = "Dato curioso del album $i"),
    ),
)

// La resena de un usuario a un album, con las copias que guarda la app
fun generateReview(usuario: UsuarioPerfilFirestoreDto, album: AlbumDto): CreateReviewDto = CreateReviewDto(
    usuarioId = usuario.id,
    itemId = album.id,
    tipo = TIPO_ALBUM,
    calificacion = 4,
    comentario = "Resena de ${usuario.nombre} a ${album.titulo}",
    fechaEscucha = "2025-01-01",
    likesCount = 0,
    commentsCount = 0,
    createdAt = "2025-01-01T00:00:00Z",
    updatedAt = "2025-01-01T00:00:00Z",
    usuario = CreateReviewUsuarioDto(nombre = usuario.nombre, fotoUrl = usuario.fotoUrl),
    item = CreateReviewItemDto(titulo = album.titulo, subtitulo = album.artista, imagenUrl = album.portadaUrl),
)

// Carga 10 usuarios, 5 albumes con sus datos curiosos y la resena "review_i" del usuario i al
// album i (i de 1 a 5)
suspend fun cargarDatosDePrueba(db: FirebaseFirestore) {
    val batch = db.batch()

    repeat(10) { i ->
        val usuario = generateUsuario(i)
        batch.set(db.collection("usuarios").document(usuario.id), usuario)
    }

    for (i in 1..5) {
        val album = generateAlbum(i)
        batch.set(db.collection("albumes").document(album.id), album)
        batch.set(db.collection("historias").document(album.id), generateHistoria(i))
        batch.set(db.collection("reviews").document("review_$i"), generateReview(generateUsuario(i), album))
    }

    batch.commit().await()
}

// Antes y despues de cada prueba la base debe quedar igual, y lo ideal es que vacia. Borrar un
// documento no borra sus subcolecciones: se recorren una por una
suspend fun limpiarBase(db: FirebaseFirestore) {
    val usuarios = db.collection("usuarios").get().await()
    for (usuarioDoc in usuarios) {
        for (subcoleccion in listOf("followers", "following", "likes", "guardados", "notificaciones")) {
            val documentos = usuarioDoc.reference.collection(subcoleccion).get().await()
            for (documento in documentos) documento.reference.delete().await()
        }

        usuarioDoc.reference.delete().await()
    }

    val reviews = db.collection("reviews").get().await()
    for (reviewDoc in reviews) {
        val likes = reviewDoc.reference.collection("likes").get().await()
        for (like in likes) like.reference.delete().await()

        val comentarios = reviewDoc.reference.collection("comments").get().await()
        for (comentario in comentarios) {
            val likesComentario = comentario.reference.collection("likes").get().await()
            for (like in likesComentario) like.reference.delete().await()

            comentario.reference.delete().await()
        }

        reviewDoc.reference.delete().await()
    }

    for (coleccion in listOf("albumes", "canciones", "artistas", "historias")) {
        val items = db.collection(coleccion).get().await()
        for (itemDoc in items) {
            val likes = itemDoc.reference.collection("likes").get().await()
            for (like in likes) like.reference.delete().await()

            itemDoc.reference.delete().await()
        }
    }
}
