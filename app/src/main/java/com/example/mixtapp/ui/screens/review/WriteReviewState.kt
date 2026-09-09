package com.example.mixtapp.ui.screens.review

import com.example.mixtapp.ui.screens.review.model.ReviewAlbumUi

// El album es nulable porque puede que el id no exista
data class WriteReviewState(
    val album: ReviewAlbumUi? = null,
    val rating: Int = 0,
    val reviewText: String = "",
    val selectedMoods: List<String> = emptyList(),
    val moods: List<String> = emptyList(),
    val listenedDate: String = "",
    val isFavorite: Boolean = false,
    val hasPosted: Boolean = false,
)
