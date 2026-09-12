package com.example.mixtapp.ui.screens.discussion

import com.example.mixtapp.ui.screens.discussion.model.DiscussionCommentUi
import com.example.mixtapp.ui.screens.discussion.model.DiscussionUi

// La discusion es nulable porque puede que el id de la resena no exista
data class DiscussionState(
    val discussion: DiscussionUi? = null,
    val comentarios: List<DiscussionCommentUi> = emptyList(),
    val nuevoComentario: String = "",
    val isReviewLiked: Boolean = false,
    val isReviewShared: Boolean = false,
    val likedCommentIds: Set<String> = emptySet(),
    val replyingToCommentId: String? = null,
)
