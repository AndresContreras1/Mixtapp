package com.example.mixtapp.ui.screens.home.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

data class HomeFilterUi(
    val id: String,
    @StringRes val label: Int
)

const val FILTRO_PARA_TI = "paraTi"
const val FILTRO_TENDENCIAS = "tendencias"
const val FILTRO_AMIGOS = "amigos"

val homeFilters = listOf(
    HomeFilterUi(id = FILTRO_PARA_TI, label = R.string.for_you),
    HomeFilterUi(id = FILTRO_TENDENCIAS, label = R.string.trending),
    HomeFilterUi(id = FILTRO_AMIGOS, label = R.string.friends)
)
