package com.example.mixtapp.data.dtos

data class CreateReviewDto(
    val usuarioId: Int,
    val albumId: Int,
    val calificacion: Int,
    val comentario: String,
    val fechaEscucha: String?,
)
