package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi

data class HomeState(
    // Los albumes del filtro elegido, para la fila de populares
    val albums: List<AlbumReviewsUi> = emptyList(),
    // Todos los albumes guardados, sin filtrar
    val todos: List<AlbumReviewsUi> = emptyList(),
    val trending: AlbumReviewsUi? = null,
    // Las resenas de los usuarios que sigo, la mas reciente primero
    val resenasDeSeguidos: List<FriendActivityUi> = emptyList(),
    val followedIds: Set<String> = emptySet(),
    val filters: List<HomeFilterUi> = emptyList(),
    val selectedFilterId: String = "",
    // Con el filtro de seguidos el inicio muestra solo las resenas de quienes sigo
    val soloSeguidos: Boolean = false,
    val profileImageUrl: String = "",
    // Arranca cargando porque lo primero que hace el ViewModel es escuchar los albumes
    val isLoading: Boolean = true,
    // El error de los albumes tapa la pantalla si no hay nada que mostrar; el de los seguidos
    // va aparte para que un flujo no borre el error del otro
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorSeguidosRes: Int? = null,
)
