package com.example.mixtapp.ui.screens.following

import com.example.mixtapp.ui.screens.following.model.FollowingUi

// Es nulable porque puede que todavia no se hayan cargado los datos
data class FollowingState(
    val following: FollowingUi? = null,
    val friendQuery: String = "",
    val selectedFilter: String = "",
    val selectedStoryId: String? = null,
    val likedReviewIds: List<String> = emptyList(),
    val sharedReviewIds: List<String> = emptyList(),
)
