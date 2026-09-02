package com.example.mixtapp.ui.screens.review.model

import androidx.annotation.DrawableRes

data class ReviewAlbumUi(
    val id: String,
    val title: String,
    val artist: String,
    val year: String,
    val genre: String,
    @DrawableRes val coverRes: Int,
)
