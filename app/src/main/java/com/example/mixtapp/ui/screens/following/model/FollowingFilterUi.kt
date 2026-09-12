package com.example.mixtapp.ui.screens.following.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

data class FollowingFilterUi(
    val id: String,
    @StringRes val label: Int
)

const val FILTRO_TODO = "todo"
const val FILTRO_RESENAS = "resenas"
const val FILTRO_CALIFICACIONES = "calificaciones"
const val FILTRO_LISTAS = "listas"

val followingFilters = listOf(
    FollowingFilterUi(id = FILTRO_TODO, label = R.string.following_filtro_todo),
    FollowingFilterUi(id = FILTRO_RESENAS, label = R.string.following_filtro_resenas),
    FollowingFilterUi(id = FILTRO_CALIFICACIONES, label = R.string.following_filtro_calificaciones),
    FollowingFilterUi(id = FILTRO_LISTAS, label = R.string.following_filtro_listas)
)
