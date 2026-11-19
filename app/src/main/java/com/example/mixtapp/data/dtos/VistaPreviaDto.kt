package com.example.mixtapp.data.dtos

// Lo que responde la busqueda publica de Deezer (api.deezer.com/search): una lista de canciones.
// Solo se mapean los campos que usa la app; Gson ignora los demas
data class BusquedaDeezerDto(
    val data: List<CancionDeezerDto>,
)

// Una cancion de Deezer: su titulo, su artista y preview, el enlace al mp3 de 30 segundos. El enlace
// trae un token que vence, por eso se pide al tocar play y no se guarda en la base
data class CancionDeezerDto(
    val title: String,
    val preview: String,
    val artist: ArtistaDeezerDto,
)

data class ArtistaDeezerDto(
    val name: String,
)
