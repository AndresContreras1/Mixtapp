package com.example.mixtapp.ui.screens.selectalbum

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.AlbumReviewsUi

data class SelectAlbumState(
    val albums: List<AlbumReviewsUi> = emptyList(),
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
