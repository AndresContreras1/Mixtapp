package com.example.mixtapp.data.model

data class ProfileUi(
    val id: String,
    val username: String,
    val joinDate: String,
    val albumsCount: Int,
    val listsCount: Int,
    val favoriteAlbums: List<Album>,
    // Altura de cada barra del grafico de calificaciones, de 0f a 1f
    val ratingBars: List<Float>,
)
