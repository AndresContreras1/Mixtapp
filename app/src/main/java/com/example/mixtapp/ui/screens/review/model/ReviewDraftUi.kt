package com.example.mixtapp.ui.screens.review.model

data class ReviewDraftUi(
    val rating: Int,
    val review: String,
    val moods: List<String>,
    val listenedDate: String,
    val isFavorite: Boolean,
)
