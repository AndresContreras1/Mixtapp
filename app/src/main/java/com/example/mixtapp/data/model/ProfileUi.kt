package com.example.mixtapp.data.model

data class ProfileUi(
    val id: String,
    val username: String,
    val joinDate: String,
    val reviewsCount: Int,
    val albumsCount: Int,
    val listsCount: Int,
    val favoriteAlbums: List<Album>,
    val recentActivity: RecentActivityUi,
    // Altura de cada barra del grafico de calificaciones, de 0f a 1f
    val ratingBars: List<Float>,
)
