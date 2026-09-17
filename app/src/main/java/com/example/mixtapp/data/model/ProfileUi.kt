package com.example.mixtapp.data.model

data class ProfileUi(
    val id: String,
    val username: String,
    val joinDate: String,
    val reviewsCount: Int,
    val albumsCount: Int,
    val listsCount: Int,
    // Cuantos espacios de favorito se dibujan en la fila
    val favoritesCount: Int,
    val recentActivity: RecentActivityUi,
    // Altura de cada barra del grafico de calificaciones, de 0f a 1f
    val ratingBars: List<Float>,
)

data class RecentActivityUi(
    val id: String,
    val album: Album,
    val rating: Int,
    val comment: String,
)
