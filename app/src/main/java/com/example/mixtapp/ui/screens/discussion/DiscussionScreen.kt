package com.example.mixtapp.ui.screens.discussion

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.ui.screens.discussion.components.NewCommentField
import com.example.mixtapp.ui.screens.discussion.components.ThreadCommentItem
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ReviewCard
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.SectionTitle

// Recibe solo el id de la resena; el ViewModel se encarga de escuchar la discusion
@Composable
fun DiscussionScreen(
    reviewId: String,
    discussionViewModel: DiscussionViewModel,
    onAuthorClick: (String) -> Unit,
    // El tipo y el id de lo que se califico, para abrir su detalle
    onItemClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by discussionViewModel.uiState.collectAsState()

    // Necesita el id que llega por la ruta, asi que empieza a escuchar en un LaunchedEffect
    LaunchedEffect(Unit) {
        discussionViewModel.getDiscussionByReviewId(reviewId = reviewId)
    }

    DiscussionScreenContent(
        isLoading = state.isLoading,
        discussion = state.discussion,
        nuevoComentario = state.nuevoComentario,
        replyingToCommentId = state.replyingToCommentId,
        publicando = state.publicando,
        errorMessageRes = state.errorMessageRes,
        errorAccionRes = state.errorAccionRes,
        onBackClick = onBackClick,
        onAuthorClick = onAuthorClick,
        onItemClick = onItemClick,
        onReviewLikeClick = { discussionViewModel.sendOrDeleteLike(reviewId = it) },
        onCommentLikeClick = { discussionViewModel.sendOrDeleteLikeComentario(commentId = it) },
        onCommentReplyClick = { discussionViewModel.responderComentario(commentId = it) },
        onCancelReplyClick = { discussionViewModel.cancelarRespuesta() },
        onNuevoComentarioChange = { discussionViewModel.updateNuevoComentario(texto = it) },
        onPublicarComentarioClick = { discussionViewModel.publicarComentario() },
        modifier = modifier,
    )
}

@Composable
fun DiscussionScreenContent(
    isLoading: Boolean,
    discussion: DiscussionUi?,
    nuevoComentario: String,
    replyingToCommentId: String?,
    publicando: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorAccionRes: Int?,
    onBackClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onItemClick: (String, String) -> Unit,
    onReviewLikeClick: (String) -> Unit,
    onCommentLikeClick: (String) -> Unit,
    onCommentReplyClick: (String) -> Unit,
    onCancelReplyClick: () -> Unit,
    onNuevoComentarioChange: (String) -> Unit,
    onPublicarComentarioClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            isLoading -> LoadingBox()
            discussion == null -> ErrorBox(messageRes = errorMessageRes ?: R.string.discusion_no_encontrada)
            else -> {
                // El nombre de quien escribio el comentario al que se responde
                val respondiendoA = discussion.comments.find { it.id == replyingToCommentId }?.author

                // El campo para comentar queda fijo abajo y sube con el teclado
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()
                ) {
                    ScreenTitle(
                        title = stringResource(R.string.discussion),
                        onBackClick = onBackClick,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 8.dp, bottom = 16.dp),
                    ) {
                        item {
                            // Ya estamos en la discusion: el icono de comentarios solo muestra el total
                            ReviewCard(
                                review = discussion.review,
                                onAuthorClick = onAuthorClick,
                                onLikeClick = onReviewLikeClick,
                                onCommentsClick = {},
                                mostrarItem = true,
                                onItemClick = { item -> onItemClick(item.tipo, item.id) },
                            )
                        }

                        item {
                            SectionTitle(
                                title = stringResource(R.string.comentarios_total, discussion.comments.size),
                                modifier = Modifier.padding(top = 24.dp, bottom = 4.dp),
                            )
                        }

                        if (discussion.comments.isEmpty()) {
                            item {
                                EmptyMessage(
                                    messageRes = R.string.sin_comentarios,
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }

                        // Ya llegan en hilo: cada comentario seguido de sus respuestas
                        items(discussion.comments, key = { it.id }) { comment ->
                            ThreadCommentItem(
                                comment = comment,
                                isReplying = replyingToCommentId == comment.id,
                                onAuthorClick = onAuthorClick,
                                onLikeClick = { onCommentLikeClick(comment.id) },
                                onReplyClick = { onCommentReplyClick(comment.id) },
                            )
                        }
                    }

                    ErrorMessage(
                        messageRes = errorAccionRes,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )

                    NewCommentField(
                        value = nuevoComentario,
                        respondiendoA = respondiendoA,
                        publicando = publicando,
                        onValueChange = onNuevoComentarioChange,
                        onCancelReplyClick = onCancelReplyClick,
                        onPublicarClick = onPublicarComentarioClick,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 12.dp),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun DiscussionScreenPreview() {
    MixtappTheme(darkTheme = true) {
        DiscussionScreenContent(
            isLoading = false,
            discussion = LocalPreviewProvider.discussion,
            nuevoComentario = "",
            replyingToCommentId = null,
            publicando = false,
            errorMessageRes = null,
            errorAccionRes = null,
            onBackClick = {},
            onAuthorClick = {},
            onItemClick = { _, _ -> },
            onReviewLikeClick = {},
            onCommentLikeClick = {},
            onCommentReplyClick = {},
            onCancelReplyClick = {},
            onNuevoComentarioChange = {},
            onPublicarComentarioClick = {},
        )
    }
}
