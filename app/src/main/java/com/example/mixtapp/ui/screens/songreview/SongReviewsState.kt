package com.example.mixtapp.ui.screens.songreview

import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi

// La cancion es nulable porque puede que el id no exista
data class SongReviewsState(
    val song: SongReviewUi? = null,
    val userRating: Int = 0,
    val isSaved: Boolean = false,
    val isLiked: Boolean = false,
)
