package com.example.mixtapp.ui.screens.home.model

import com.example.mixtapp.R
import com.example.mixtapp.ui.utils.OpcionUi

// Lo mas popular de los generos que me gustan (mis likes y mis resenas)
const val FILTRO_PARA_TI = "paraTi"

// Musica nueva para mi: de mis generos, pero sin mi like ni mi resena todavia
const val FILTRO_DESCUBRE = "descubre"

// Lo que esta en la lista de Deezer de mis generos, por su puesto
const val FILTRO_TENDENCIAS = "tendencias"

// Lo que resenaron los usuarios que sigo
const val FILTRO_SEGUIDOS = "seguidos"

// Los filtros del inicio: cada uno cambia lo que muestran las filas de canciones, albumes y artistas
val homeFilters = listOf(
    OpcionUi(id = FILTRO_PARA_TI, label = R.string.for_you),
    OpcionUi(id = FILTRO_DESCUBRE, label = R.string.descubre),
    OpcionUi(id = FILTRO_TENDENCIAS, label = R.string.trending),
    OpcionUi(id = FILTRO_SEGUIDOS, label = R.string.friends)
)
