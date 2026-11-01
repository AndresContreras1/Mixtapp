package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.TIPO_ALBUM

// Los mismos campos sirven para la tabla albumes del backend y la coleccion albumes de Firestore.
// El backend no tiene subgenero, datos curiosos, tendencia ni likes: Gson usa el constructor vacio
// y esos campos se quedan con su valor de ahi
data class AlbumDto(
    val id: String,
    val titulo: String,
    val artista: String,
    val portadaUrl: String,
    val anio: Int,
    val genero: String,
    val subgenero: String,
    // En Firestore es un arreglo de textos
    val datosCuriosos: List<String>,
    // Puesto en el Top Colombia; null si no esta en el top
    val tendencia: Int?,
    // Quien dio el like esta en la subcoleccion likes; aqui solo cuantos son
    val likesCount: Int,
    val createdAt: String?,
    val updatedAt: String?,
    // No esta en la base: el data source lo llena buscando mi uid en la subcoleccion likes
    var liked: Boolean = false,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", "", 0, "", "", emptyList(), null, 0, null, null)
}

fun AlbumDto.toItemUi(): ItemUi {
    return ItemUi(
        id = id,
        tipo = TIPO_ALBUM,
        titulo = titulo,
        subtitulo = artista,
        detalle = "",
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
