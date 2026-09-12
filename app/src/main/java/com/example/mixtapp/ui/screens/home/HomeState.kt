package com.example.mixtapp.ui.screens.home

import com.example.mixtapp.ui.screens.home.model.FriendActivityUi
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi

data class HomeState(
    val albums: List<SongReviewUi> = emptyList(),
    val trending: SongReviewUi? = null,
    val friendActivity: FriendActivityUi? = null,
    val filters: List<HomeFilterUi> = emptyList(),
    val selectedFilterId: String = "",
    val profileImageUrl: String = "",
)
