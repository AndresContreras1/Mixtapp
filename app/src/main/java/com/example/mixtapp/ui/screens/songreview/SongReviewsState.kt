package com.example.mixtapp.ui.screens.songreview

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.SongReviewUi

// La cancion es nulable porque puede que el id no exista
data class SongReviewsState(
    val song: SongReviewUi? = null,
    @StringRes val errorMessageRes: Int? = null,
)
