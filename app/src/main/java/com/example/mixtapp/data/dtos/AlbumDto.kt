package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.Album

// Los mismos campos sirven para la tabla albumes del backend y la coleccion albumes de Firestore
data class AlbumDto(
    val id: String,
    val titulo: String,
    val artista: String,
    val portadaUrl: String,
    val anio: Int,
    val genero: String,
    val createdAt: String?,
    val updatedAt: String?,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", "", 0, "", null, null)
}

// Lo que se muestra del album dentro de una resena (el include del backend o la copia en Firestore)
data class AlbumDeResenaDto(
    val id: String,
    val titulo: String,
    val artista: String,
    val portadaUrl: String,
) {
    constructor() : this("", "", "", "")
}

fun AlbumDto.toAlbum(): Album {
    return Album(
        id = id,
        title = titulo,
        artist = artista,
        cover = portadaUrl,
        year = anio.toString(),
        genre = genero,
        tags = listOf(anio.toString(), genero),
    )
}

// El id llega aparte porque en Firestore la copia del album no lo guarda
fun AlbumDeResenaDto.toAlbum(albumId: String): Album {
    return Album(
        id = albumId,
        title = titulo,
        artist = artista,
        cover = portadaUrl,
        year = "",
        genre = "",
        tags = emptyList(),
    )
}
