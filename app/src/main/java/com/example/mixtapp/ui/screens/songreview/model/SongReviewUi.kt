package com.example.mixtapp.ui.screens.songreview.model

data class SongReviewUi(
    val id: String,
    val cover: String,
    val tags: List<String>,
    val title: String,
    val artist: String,
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
    val daysAgo: String,
    val rating: Int,
    val content: String,
    val likes: Int,
)
