package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.TIPO_ARTISTA

// Igual al documento de la coleccion artistas de Firestore y a la tabla artistas del backend
data class ArtistaDto(
    val id: String,
    val nombre: String,
    // La banda del artista; null si es solista
    val grupo: String?,
    val fotoUrl: String,
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
    constructor() : this("", "", null, "", "", "", emptyList(), null, 0, null, null)
}

// Debajo del nombre va su banda; si es solista, su genero. Los artistas no tienen anio
fun ArtistaDto.toItemUi(): ItemUi {
    return ItemUi(
        id = id,
        tipo = TIPO_ARTISTA,
        titulo = nombre,
        subtitulo = grupo ?: genero,
        detalle = "",
        imagenUrl = fotoUrl,
        anio = "",
        genero = genero,
        subgenero = subgenero,
        datosCuriosos = datosCuriosos,
        tendencia = tendencia,
        likes = likesCount,
        likesText = formatoCantidad(likesCount),
        isLiked = liked,
    )
}
