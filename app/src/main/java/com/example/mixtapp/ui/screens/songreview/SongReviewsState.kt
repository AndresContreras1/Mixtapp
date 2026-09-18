package com.example.mixtapp.ui.screens.songreview

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.SongReviewUi

// La cancion es nulable porque puede que el id no exista
data class SongReviewsState(
    val song: SongReviewUi? = null,
    val userRating: Int = 0,
    val isSaved: Boolean = false,
    val isLiked: Boolean = false,
    val likedReviewIds: Set<String> = emptySet(),
    @StringRes val errorMessageRes: Int? = null,
)
