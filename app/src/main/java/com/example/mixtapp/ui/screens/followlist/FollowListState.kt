package com.example.mixtapp.ui.screens.followlist

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.UsuarioUi

// Que lista se abre: llega por la ruta junto al id del usuario
const val LISTA_SEGUIDORES = "seguidores"
const val LISTA_SEGUIDOS = "seguidos"

data class FollowListState(
    val usuarios: List<UsuarioUi> = emptyList(),
    // Arranca cargando porque lo primero que hace la pantalla es pedir la lista
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)
