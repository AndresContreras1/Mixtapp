package com.example.mixtapp.data.model

data class MixtapeUi(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val usuarioId: String = "",
    val usuarioNombre: String = "",
    val usuarioFotoUrl: String = "",
    val items: List<ItemResumenUi> = emptyList(),
    val itemCount: Int = 0,
    val isMine: Boolean = false,
    val likes: Int = 0,
    val likesText: String = "0",
    val isLiked: Boolean = false,
    val createdAt: String = "",
    val timeAgo: String = "",
)
