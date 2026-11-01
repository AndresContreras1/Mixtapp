package com.example.mixtapp.ui.screens.discussion

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.DiscussionUi

// La discusion es nulable porque puede que el id de la resena no exista
data class DiscussionState(
    val discussion: DiscussionUi? = null,
    val nuevoComentario: String = "",
    // El comentario al que se responde; null si se comenta la resena
    val replyingToCommentId: String? = null,
    // Mientras se guarda el comentario no se manda otro
    val publicando: Boolean = false,
    // Arranca cargando porque lo primero que hace la pantalla es pedir la discusion
    val isLoading: Boolean = true,
    // Error de la pantalla: no se pudo cargar la discusion
    @StringRes val errorMessageRes: Int? = null,
    // Error de un boton (like, publicar): la discusion se sigue mostrando
    @StringRes val errorAccionRes: Int? = null,
)
