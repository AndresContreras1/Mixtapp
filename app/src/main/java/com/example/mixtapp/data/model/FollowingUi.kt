package com.example.mixtapp.data.model

data class FollowingUi(
    val id: String,
    val followingCount: Int,
    val followersCount: Int,
    // Los usuarios que sigo, para la fila de fotos de arriba
    val seguidos: List<UsuarioUi>,
    // Sus resenas, la ultima publicada o editada primero
    val reviews: List<ReviewUi>,
)
