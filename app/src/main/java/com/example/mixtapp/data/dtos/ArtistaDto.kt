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
    // El usuario de Instagram sin la @: el de su banda si es integrante. null si no tiene
    val instagram: String?,
    val tendencia: Int?,
    val likesCount: Int,
    val createdAt: String?,
    val updatedAt: String?,
    // No esta en la base: el data source lo llena buscando mi uid en la subcoleccion likes
    var liked: Boolean = false,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", null, "", "", "", null, null, 0, null, null)
}

// Debajo del nombre va su banda; si es solista, su genero. Los artistas no tienen anio ni fecha
fun ArtistaDto.toItemUi(): ItemUi {
    return ItemUi(
        id = id,
        tipo = TIPO_ARTISTA,
        titulo = nombre,
        subtitulo = grupo ?: genero,
        detalle = "",
        imagenUrl = fotoUrl,
        anio = "",
        fechaLanzamiento = "",
        artista = grupo ?: nombre,
        genero = genero,
        subgenero = subgenero,
        tendencia = tendencia,
        likes = likesCount,
        likesText = formatoCantidad(likesCount),
        isLiked = liked,
        spotifyUrl = busquedaEnSpotify(grupo ?: nombre),
        instagram = instagram ?: "",
        instagramUrl = perfilDeInstagram(instagram),
    )
}
