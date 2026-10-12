package com.example.mixtapp.ui.screens.songreview

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.SongReviewUi

// El album es nulable porque puede que el id no exista
data class SongReviewsState(
    val song: SongReviewUi? = null,
    // Mi resena de este album, si ya la escribi
    val miResenaId: String? = null,
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
