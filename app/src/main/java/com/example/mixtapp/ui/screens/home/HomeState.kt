package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi

data class HomeState(
    // Los albumes del filtro elegido, para la fila de populares
    val albums: List<SongReviewUi> = emptyList(),
    // Todos los albumes guardados, sin filtrar
    val todos: List<SongReviewUi> = emptyList(),
    val trending: SongReviewUi? = null,
    val friendActivity: FriendActivityUi? = null,
    val filters: List<HomeFilterUi> = emptyList(),
    val selectedFilterId: String = "",
    val profileImageUrl: String = "",
    // Arranca cargando porque lo primero que hace la pantalla es pedir los albumes
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)
