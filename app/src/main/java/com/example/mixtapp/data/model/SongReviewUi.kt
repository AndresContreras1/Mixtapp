package com.example.mixtapp.data.model

// Las estadisticas se calculan con las resenas guardadas del album
data class SongReviewUi(
    val id: String,
    val album: Album,
    val rating: Double,
    // El promedio ya redondeado, para pintar las estrellas
    val estrellas: Int,
    val ratingCount: String,
    val recommendRate: Int,
    val userRating: Int,
    val reviews: List<SongReviewItemUi>,
)

data class SongReviewItemUi(
    val id: String,
    val authorId: String,
    val author: String,
    val authorImage: String,
    val fecha: String,
    val rating: Int,
    val content: String,
    // Si la escribio el usuario de la app: el repositorio la marca al mapear
    val esMia: Boolean,
)
