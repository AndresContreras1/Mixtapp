package com.example.mixtapp.ui.screens.review.model

import androidx.annotation.DrawableRes
import com.example.mixtapp.R

data class ReviewAlbumUi(
    val title: String,
    val artist: String,
    val year: String,
    val genre: String,
    @DrawableRes val coverRes: Int,
)

val fromZeroAlbum = ReviewAlbumUi(
    title = "From Zero",
    artist = "Linkin Park",
    year = "2024",
    genre = "Rock",
    coverRes = R.drawable.from_zero,
)
