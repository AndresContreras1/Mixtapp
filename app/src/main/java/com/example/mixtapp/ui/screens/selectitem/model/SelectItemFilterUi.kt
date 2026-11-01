package com.example.mixtapp.ui.screens.selectitem.model

import androidx.annotation.StringRes
import com.example.mixtapp.R
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION

// Un filtro de Elegir que calificar: todos los items o solo los de un tipo
data class SelectItemFilterUi(
    val id: String,
    @StringRes val label: Int
)

// Los demas filtros usan el tipo del item como id
const val FILTRO_TODOS = "todos"

val selectItemFilters = listOf(
    SelectItemFilterUi(id = FILTRO_TODOS, label = R.string.filtro_todos),
    SelectItemFilterUi(id = TIPO_ALBUM, label = R.string.filtro_albumes),
    SelectItemFilterUi(id = TIPO_CANCION, label = R.string.filtro_canciones),
    SelectItemFilterUi(id = TIPO_ARTISTA, label = R.string.filtro_artistas)
)
