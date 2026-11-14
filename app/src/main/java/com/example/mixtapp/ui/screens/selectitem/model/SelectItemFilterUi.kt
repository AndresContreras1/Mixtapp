package com.example.mixtapp.ui.screens.selectitem.model

import com.example.mixtapp.R
import com.example.mixtapp.ui.utils.OpcionUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION

// Los filtros de Elegir que calificar: todos los items o solo los de un tipo. Los demas filtros
// usan el tipo del item como id
const val FILTRO_TODOS = "todos"

val selectItemFilters = listOf(
    OpcionUi(id = FILTRO_TODOS, label = R.string.filtro_todos),
    OpcionUi(id = TIPO_ALBUM, label = R.string.filtro_albumes),
    OpcionUi(id = TIPO_CANCION, label = R.string.filtro_canciones),
    OpcionUi(id = TIPO_ARTISTA, label = R.string.filtro_artistas)
)
