package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.Album

// Igual a la tabla albumes del backend
data class AlbumDto(
    val id: Int,
    val titulo: String,
    val artista: String,
    val portadaUrl: String,
    val anio: Int,
    val genero: String,
    val createdAt: String,
    val updatedAt: String,
)

// Lo que trae el include del album dentro de las resenas de un usuario
data class AlbumDeResenaDto(
    val id: Int,
    val titulo: String,
    val artista: String,
    val portadaUrl: String,
)

fun AlbumDto.toAlbum(): Album {
    return Album(
        id = id.toString(),
        title = titulo,
        artist = artista,
        cover = portadaUrl,
        year = anio.toString(),
        genre = genero,
        tags = listOf(anio.toString(), genero),
    )
}

fun AlbumDeResenaDto.toAlbum(): Album {
    return Album(
        id = id.toString(),
        title = titulo,
        artist = artista,
        cover = portadaUrl,
        year = "",
        genre = "",
        tags = emptyList(),
    )
}
