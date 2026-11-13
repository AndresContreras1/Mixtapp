package com.example.mixtapp.data.model

// Un punto del mapa: latitud y longitud en grados
data class Coordenadas(
    val latitud: Double,
    val longitud: Double,
)

// Un marcador del mapa: la resena (para la tarjeta que sale al tocarlo) y donde se publico
data class ResenaEnMapaUi(
    val review: ReviewUi,
    val latitud: Double,
    val longitud: Double,
)
