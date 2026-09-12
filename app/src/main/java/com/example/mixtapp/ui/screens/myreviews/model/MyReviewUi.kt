package com.example.mixtapp.ui.screens.myreviews.model

data class MyReviewUi(
    val id: String,
    val songId: String,      // el album que abre esta resena
    val title: String,
    val artist: String,
    val cover: String,
    val rating: Int,          // 0..5
    val excerpt: String,
    val tags: List<String>,
    val duration: String,     // "1'08", "1'50"
    val date: String,         // "Aug 18, 2026"
)