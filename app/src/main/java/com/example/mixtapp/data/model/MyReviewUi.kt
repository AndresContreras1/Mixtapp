package com.example.mixtapp.data.model

data class MyReviewUi(
    val id: String,
    val album: Album,
    val rating: Int,          // 0..5
    val excerpt: String,
    val tags: List<String>,
    val date: String,         // "Aug 18, 2026"
)
