package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi

data class HomeState(
    // Los albumes del filtro elegido, para la fila de populares
    val albums: List<AlbumReviewsUi> = emptyList(),
    // Todos los albumes guardados, sin filtrar
    val todos: List<AlbumReviewsUi> = emptyList(),
    val trending: AlbumReviewsUi? = null,
    val friendActivity: FriendActivityUi? = null,
    val followedIds: Set<String> = emptySet(),
    val filters: List<HomeFilterUi> = emptyList(),
    val selectedFilterId: String = "",
    val profileImageUrl: String = "",
    // Arranca cargando porque lo primero que hace la pantalla es pedir los albumes
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)
