package com.example.mixtapp.ui.screens.selectalbum

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.SongReviewUi

data class SelectAlbumState(
    val albums: List<SongReviewUi> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
