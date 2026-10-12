package com.example.mixtapp.data.model

data class MyReviewUi(
    val id: String,
    val album: Album,
    val rating: Int,          // 0..5
    val excerpt: String,
    val date: String,         // AAAA-MM-DD, como la guarda el backend
)
