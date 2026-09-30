package com.example.mixtapp.ui.screens.discussion

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.DiscussionUi

// La discusion es nulable porque puede que el id de la resena no exista
data class DiscussionState(
    val discussion: DiscussionUi? = null,
    val nuevoComentario: String = "",
    val replyingToCommentId: String? = null,
    @StringRes val errorMessageRes: Int? = null,
)
