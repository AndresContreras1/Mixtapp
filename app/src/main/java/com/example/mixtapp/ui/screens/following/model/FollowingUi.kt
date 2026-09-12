package com.example.mixtapp.ui.screens.following.model

data class FollowingUi(
    val id: String,
    val followingCount: Int,
    val followersCount: Int,
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
    val cover: String,
    val albumTitle: String,
    val artistName: String,
    val rating: Int,
    val reviewText: String,
    val likes: Int,
    val comments: Int,
    val isLiked: Boolean,
    val isShared: Boolean,
)
