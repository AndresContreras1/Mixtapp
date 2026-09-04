package com.example.mixtapp.ui.screens.following.model

data class FollowingUi(
    val followingCount: Int,
    val followersCount: Int,
    val filters: List<String>,
    val stories: List<FollowingStoryUi>,
    val reviews: List<FollowingReviewUi>,
)

data class FollowingStoryUi(
    val id: String,
    val label: String,
    val initials: String,
    val isAddAction: Boolean,
)

data class FollowingReviewUi(
    val id: String,
    val reviewerName: String,
    val reviewerInitials: String,
    val reviewedAt: String,
    val coverRes: Int,
    val albumTitle: String,
    val artistName: String,
    val rating: Int,
    val reviewText: String,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean,
    val isShared: Boolean,
)
