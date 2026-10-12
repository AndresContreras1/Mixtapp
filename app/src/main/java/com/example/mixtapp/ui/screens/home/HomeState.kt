package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi

data class HomeState(
    // Los albumes del filtro elegido, para la fila de populares
    val albums: List<SongReviewUi> = emptyList(),
    // Todos los albumes del backend, sin filtrar
    val todos: List<SongReviewUi> = emptyList(),
    val trending: SongReviewUi? = null,
    val friendActivity: FriendActivityUi? = null,
    val filters: List<HomeFilterUi> = emptyList(),
    val selectedFilterId: String = "",
    // Para no contar mis propias resenas como actividad de amigos
    val idsDeMisResenas: List<String> = emptyList(),
    val profileImageUrl: String = "",
    val isLoading: Boolean = false,
    @StringRes val errorMessageRes: Int? = null,
)
