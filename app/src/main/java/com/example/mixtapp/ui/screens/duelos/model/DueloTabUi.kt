package com.example.mixtapp.ui.screens.duelos.model

import com.example.mixtapp.R
import com.example.mixtapp.ui.utils.OpcionUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION

// Cada dia hay un duelo de cada tipo. El id de la pestana es el tipo del item
val dueloTabs = listOf(
    OpcionUi(id = TIPO_ALBUM, label = R.string.duelo_tab_albumes),
    OpcionUi(id = TIPO_CANCION, label = R.string.duelo_tab_canciones),
    OpcionUi(id = TIPO_ARTISTA, label = R.string.duelo_tab_artistas)
)
