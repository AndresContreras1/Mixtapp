package com.example.mixtapp.ui.screens.mixtapedetail

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.MixtapeUi

data class MixtapeDetailState(
    val isLoading: Boolean = true,
    val mixtape: MixtapeUi? = null,
    @StringRes val errorMessageRes: Int? = null,
    val mensajeExitoRes: Int? = null,
)
