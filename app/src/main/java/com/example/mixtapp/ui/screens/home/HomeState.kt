package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi

data class HomeState(
    val albums: List<SongReviewUi> = emptyList(),
    val trending: SongReviewUi? = null,
    val friendActivity: FriendActivityUi? = null,
    val filters: List<HomeFilterUi> = emptyList(),
    val selectedFilterId: String = "",
    val profileImageUrl: String = "",
    @StringRes val errorMessageRes: Int? = null,
)
