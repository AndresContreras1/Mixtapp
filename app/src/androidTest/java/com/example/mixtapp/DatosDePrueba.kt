package com.example.mixtapp

import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.CreateReviewAlbumDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Los datos los crea la propia prueba, no la carga con Faker: asi cada prueba es replicable.
// Estan en un solo archivo para no copiarlos en cada clase de prueba

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
    genero = "Rock",
    createdAt = "2025-01-01T00:00:00Z",
    updatedAt = "2025-01-01T00:00:00Z",
)

// La resena de un usuario a un album, con las copias que guarda la app
fun generateReview(usuario: UsuarioPerfilFirestoreDto, album: AlbumDto): CreateReviewDto = CreateReviewDto(
    usuarioId = usuario.id,
    albumId = album.id,
    calificacion = 4,
    comentario = "Resena de ${usuario.nombre} a ${album.titulo}",
    fechaEscucha = "2025-01-01",
    likesCount = 0,
    commentsCount = 0,
    usuario = CreateReviewUsuarioDto(nombre = usuario.nombre, fotoUrl = usuario.fotoUrl),
    album = CreateReviewAlbumDto(titulo = album.titulo, artista = album.artista, portadaUrl = album.portadaUrl),
)

// Carga 10 usuarios, 5 albumes y la resena "review_i" del usuario i al album i (i de 1 a 5)
suspend fun cargarDatosDePrueba(db: FirebaseFirestore) {
    val batch = db.batch()

    repeat(10) { i ->
        val usuario = generateUsuario(i)
        batch.set(db.collection("usuarios").document(usuario.id), usuario)
    }

    for (i in 1..5) {
        val album = generateAlbum(i)
        batch.set(db.collection("albumes").document(album.id), album)
        batch.set(db.collection("reviews").document("review_$i"), generateReview(generateUsuario(i), album))
    }

    batch.commit().await()
}

// Antes y despues de cada prueba la base debe quedar igual, y lo ideal es que vacia. Borrar un
// documento no borra sus subcolecciones: se recorren una por una
suspend fun limpiarBase(db: FirebaseFirestore) {
    val usuarios = db.collection("usuarios").get().await()
    for (usuarioDoc in usuarios) {
        val followers = usuarioDoc.reference.collection("followers").get().await()
        for (f in followers) f.reference.delete().await()

        val following = usuarioDoc.reference.collection("following").get().await()
        for (f in following) f.reference.delete().await()

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

    val albumes = db.collection("albumes").get().await()
    for (albumDoc in albumes) albumDoc.reference.delete().await()
}
