package com.example.mixtapp.debug

import com.example.mixtapp.data.dtos.AlbumDto
import com.github.javafaker.Faker
import java.time.LocalDate
import java.util.Locale

// Genera datos falsos con la forma exacta de los documentos de Firestore de Mixtapp
class FakeDataFactory {

    private val faker = Faker(Locale("es"))

    fun crearUsuario(id: String): Map<String, Any?> = mapOf(
        "username" to faker.name().username(),
        "nombre" to faker.name().fullName(),
        "bio" to faker.lorem().sentence(6),
        "fotoUrl" to "https://picsum.photos/seed/$id/200",
        // Empiezan en 0 porque no se crean subcolecciones followers/following
        "followersCount" to 0,
        "followingCount" to 0,
        "fcmtoken" to ""
    )

    fun crearAlbum(id: String): AlbumDto = AlbumDto(
        id = id,
        titulo = faker.book().title(),
        artista = faker.artist().name(),
        portadaUrl = "https://picsum.photos/seed/$id/400",
        anio = faker.number().numberBetween(1990, 2026),
        genero = faker.music().genre(),
        createdAt = null,
        updatedAt = null
    )

    // La resena guarda una copia del usuario y del album porque Firestore no tiene joins
    fun crearResena(
        usuarioId: String,
        nombreUsuario: String,
        fotoUsuario: String?,
        album: AlbumDto
    ): Map<String, Any?> = mapOf(
        "usuarioId" to usuarioId,
        "albumId" to album.id,
        "calificacion" to faker.number().numberBetween(1, 6),
        "comentario" to faker.lorem().sentence(8),
        "fechaEscucha" to LocalDate.now().minusDays(faker.number().numberBetween(0, 365).toLong()).toString(),
        "likesCount" to 0,
        "usuario" to mapOf("nombre" to nombreUsuario, "fotoUrl" to fotoUsuario),
        "album" to mapOf(
            "titulo" to album.titulo,
            "artista" to album.artista,
            "portadaUrl" to album.portadaUrl
        )
    )

    companion object {
        fun idUsuario(indice: Int) = "seed_usuario_$indice"
        fun idAlbum(indice: Int) = "seed_album_$indice"
        fun idResena(album: Int, usuario: Int) = "seed_resena_${album}_$usuario"
    }
}
