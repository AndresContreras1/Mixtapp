package com.example.mixtapp.data.model

// Las estadisticas se calculan con las resenas del album que vienen del backend
data class SongReviewUi(
    val id: String,
    val album: Album,
    val rating: Double,
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
)
