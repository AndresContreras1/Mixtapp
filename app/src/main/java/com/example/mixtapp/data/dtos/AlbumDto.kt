package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.Album

data class AlbumDto(
    val id: Int,
    val titulo: String,
    val artista: String,
    val portadaUrl: String,
    val anio: Int?,
    val genero: String?,
    val createdAt: String?,
    val updatedAt: String?,
)

fun AlbumDto.toAlbum(): Album {
    return Album(
        id = id.toString(),
        title = titulo,
        artist = artista,
        cover = portadaUrl,
        year = anio?.toString() ?: "",
        genre = genero ?: "",
        tags = if (genero != null) listOf(genero) else emptyList(),
    )
}
