package com.example.mixtapp.data.model

data class Album(
    val id: String,
    val title: String,
    val artist: String,
    val cover: String,
    val year: String,
    val genre: String,
    val tags: List<String>,
)
