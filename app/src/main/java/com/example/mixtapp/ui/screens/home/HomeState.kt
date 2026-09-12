package com.example.mixtapp.ui.screens.home

import com.example.mixtapp.ui.screens.home.model.FriendActivityUi
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi

// El filtro se guarda como indice porque los nombres salen de strings.xml,
// y el ViewModel no tiene acceso a los recursos
data class HomeState(
    val albums: List<SongReviewUi> = emptyList(),
    val trending: SongReviewUi? = null,
    val friendActivity: FriendActivityUi? = null,
    val selectedFilterIndex: Int = 0,
    val profileImageUrl: String = "",
)
