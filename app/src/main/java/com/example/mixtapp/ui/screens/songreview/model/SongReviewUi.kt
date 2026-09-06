package com.example.mixtapp.ui.screens.songreview.model

import androidx.annotation.DrawableRes

data class SongReviewUi(
    val id: String,
    @DrawableRes val coverRes: Int,
    val tags: List<String>,
    val title: String,
    val artist: String,
    val rating: String,
    val ratingCount: String,
    val recommendRate: String,
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
