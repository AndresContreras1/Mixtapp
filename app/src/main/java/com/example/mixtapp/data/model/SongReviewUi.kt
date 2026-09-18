package com.example.mixtapp.data.model

data class SongReviewUi(
    val album: Album,
    val rating: Double,
    val ratingCount: String,
    val recommendRate: Int,
    val userRating: Int,
    val isSaved: Boolean,
    val isLiked: Boolean,
    val reviews: List<SongReviewItemUi>,
)

data class SongReviewItemUi(
    val id: String,
    val author: String,
    val initials: String,
    val daysAgo: String,
    val rating: Int,
    val content: String,
    val likes: Int,
    val isLiked: Boolean,
)
