package com.example.mixtapp.ui.screens.duelos.model

import androidx.annotation.StringRes
import com.example.mixtapp.R
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION

// Cada dia hay un duelo de cada tipo. El id de la pestana es el tipo del item
data class DueloTabUi(
    val tipo: String,
    @StringRes val label: Int
)

val dueloTabs = listOf(
    DueloTabUi(tipo = TIPO_ALBUM, label = R.string.duelo_tab_albumes),
    DueloTabUi(tipo = TIPO_CANCION, label = R.string.duelo_tab_canciones),
    DueloTabUi(tipo = TIPO_ARTISTA, label = R.string.duelo_tab_artistas)
)
