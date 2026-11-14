package com.example.mixtapp.ui.screens.following

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.SonandoUi

data class FollowingState(
    // Es nulable porque puede que todavia no hayan llegado los datos
    val following: FollowingUi? = null,
    // Las resenas que se ven: ya vienen filtradas por el nombre del autor
    val reviews: List<ReviewUi> = emptyList(),
    val friendQuery: String = "",
    // Se pone en true al empezar a escuchar y en false cuando llegan los datos o fallan
    val isLoading: Boolean = false,
    // No se pudo cargar la pestana: ocupa toda la pantalla
    @StringRes val errorMessageRes: Int? = null,
    // Fallo un like: la lista sigue visible y el error sale encima
    @StringRes val errorAccionRes: Int? = null,
    // Lo que escucho yo y lo que escuchan quienes sigo; su error va aparte para no tapar las resenas
    val sonando: List<SonandoUi> = emptyList(),
    @StringRes val errorSonandoRes: Int? = null,
    // Fallo un 🔥 o quitar lo que suena: va aparte para que la fila que llega no lo borre
    @StringRes val errorAccionSonandoRes: Int? = null,
)
