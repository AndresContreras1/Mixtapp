package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.TIPO_ALBUM

// Los mismos campos sirven para la tabla albumes del backend y la coleccion albumes de Firestore.
// El backend no tiene subgenero, fecha de salida, tendencia ni likes: Gson usa el constructor vacio
// y esos campos se quedan con su valor de ahi
data class AlbumDto(
    val id: String,
    val titulo: String,
    val artista: String,
    val portadaUrl: String,
    val anio: Int,
    // AAAA-MM-DD
    val fechaLanzamiento: String?,
    val genero: String,
    val subgenero: String,
    // Puesto en la lista de Deezer de su genero; null si no esta en la lista
    val tendencia: Int?,
    // Quien dio el like esta en la subcoleccion likes; aqui solo cuantos son
    val likesCount: Int,
    val createdAt: String?,
    val updatedAt: String?,
    // No esta en la base: el data source lo llena buscando mi uid en la subcoleccion likes
    var liked: Boolean = false,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", "", 0, null, "", "", null, 0, null, null)
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
        fechaLanzamiento = fechaLanzamiento ?: "",
        artista = artista,
        genero = genero,
        subgenero = subgenero,
        tendencia = tendencia,
        likes = likesCount,
        likesText = formatoCantidad(likesCount),
        isLiked = liked,
        spotifyUrl = busquedaEnSpotify("$artista $titulo"),
    )
}
