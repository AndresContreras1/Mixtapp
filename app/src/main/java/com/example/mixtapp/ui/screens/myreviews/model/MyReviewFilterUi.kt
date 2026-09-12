package com.example.mixtapp.ui.screens.myreviews.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

data class MyReviewFilterUi(
    val id: String,
    @StringRes val label: Int
)

const val FILTRO_RECIENTES = "recientes"
const val FILTRO_MEJOR_CALIFICADAS = "mejorCalificadas"
const val FILTRO_A_Z = "aZ"
const val FILTRO_CALIFICACION_5 = "calificacion5"
const val FILTRO_CALIFICACION_4 = "calificacion4"

val myReviewFilters = listOf(
    MyReviewFilterUi(id = FILTRO_RECIENTES, label = R.string.filtro_recientes),
    MyReviewFilterUi(id = FILTRO_MEJOR_CALIFICADAS, label = R.string.filtro_mejor_calificadas),
    MyReviewFilterUi(id = FILTRO_A_Z, label = R.string.filtro_a_z),
    MyReviewFilterUi(id = FILTRO_CALIFICACION_5, label = R.string.filtro_calificacion_5),
    MyReviewFilterUi(id = FILTRO_CALIFICACION_4, label = R.string.filtro_calificacion_4)
)
