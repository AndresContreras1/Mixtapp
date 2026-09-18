package com.example.mixtapp.ui.screens.following

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.ui.screens.following.model.FollowingFilterUi

// Es nulable porque puede que todavia no se hayan cargado los datos
data class FollowingState(
    val following: FollowingUi? = null,
    val reviews: List<FollowingReviewUi> = emptyList(),
    val friendQuery: String = "",
    val filters: List<FollowingFilterUi> = emptyList(),
    val selectedFilterId: String = "",
    val selectedStoryId: String? = null,
    val likedReviewIds: Set<String> = emptySet(),
    val sharedReviewIds: Set<String> = emptySet(),
    @StringRes val errorMessageRes: Int? = null,
)
