package com.example.mixtapp.ui.screens.discussion.model

data class DiscussionUi(
    val review: DiscussionReviewUi,
    val comments: List<DiscussionCommentUi>,
)

data class DiscussionReviewUi(
    val id: String,
    val reviewerName: String,
    val reviewerAvatarText: String,
    val reviewedAt: String,
    val coverRes: Int,
    val albumTitle: String,
    val artistName: String,
    val rating: Int,
    val reviewText: String,
    val likes: Int,
    val commentsCount: Int,
    val isLiked: Boolean,
    val isShared: Boolean,
)

data class DiscussionCommentUi(
    val id: String,
    val author: String,
    val initials: String,
    val timeAgo: String,
    val content: String,
    val likes: Int,
    val isReply: Boolean = false,
    val isLiked: Boolean = false,
)
