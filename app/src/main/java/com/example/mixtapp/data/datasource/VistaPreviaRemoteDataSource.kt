package com.example.mixtapp.data.datasource

// De donde sale el fragmento de 30 segundos de una cancion, un album o un artista
interface VistaPreviaRemoteDataSource {
    // El enlace al mp3 de la primera cancion que coincide con la busqueda; null si no hay ninguna o
    // si la que coincide no tiene fragmento
    suspend fun getVistaPrevia(busqueda: String): String?
}
