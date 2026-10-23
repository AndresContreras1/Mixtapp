package com.example.mixtapp.data.model

data class MyReviewUi(
    val id: String,
    val likes: Int = 0,
    val album: Album,
    val rating: Int,          // 0..5
    val excerpt: String,
    val date: String,         // AAAA-MM-DD, como la guarda la base
)
