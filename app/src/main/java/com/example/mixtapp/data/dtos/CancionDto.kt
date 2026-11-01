package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.TIPO_CANCION

// Igual al documento de la coleccion canciones de Firestore y a la tabla canciones del backend.
// album es el nombre del album como texto; albumId solo existe en Firestore
data class CancionDto(
    val id: String,
    val titulo: String,
    val albumId: String,
    val album: String,
    val artista: String,
    val portadaUrl: String,
    val anio: Int,
    val genero: String,
    val subgenero: String,
    val datosCuriosos: List<String>,
    val tendencia: Int?,
    val likesCount: Int,
    val createdAt: String?,
    val updatedAt: String?,
    // No esta en la base: el data source lo llena buscando mi uid en la subcoleccion likes
    var liked: Boolean = false,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", "", "", "", 0, "", "", emptyList(), null, 0, null, null)
}

// La cancion se muestra en tres lineas: titulo, album y artista
fun CancionDto.toItemUi(): ItemUi {
    return ItemUi(
        id = id,
        tipo = TIPO_CANCION,
        titulo = titulo,
        subtitulo = album,
        detalle = artista,
        imagenUrl = portadaUrl,
        anio = anio.toString(),
        genero = genero,
        subgenero = subgenero,
        datosCuriosos = datosCuriosos,
        tendencia = tendencia,
        likes = likesCount,
        likesText = formatoCantidad(likesCount),
        isLiked = liked,
    )
}
