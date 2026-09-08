package com.example.mixtapp.ui.screens.discussion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.ui.screens.discussion.components.CommentsDivider
import com.example.mixtapp.ui.screens.discussion.components.DiscussionHeader
import com.example.mixtapp.ui.screens.discussion.components.DiscussionReviewCard
import com.example.mixtapp.ui.screens.discussion.components.ThreadCommentItem
import com.example.mixtapp.ui.screens.discussion.model.DiscussionUi
import com.example.mixtapp.ui.theme.DeepBackground
import com.example.mixtapp.ui.theme.MixtappTheme

// Recibe solo el id de la resena; el ViewModel se encarga de buscar la discusion
@Composable
fun DiscussionScreen(
    reviewId: String,
    discussionViewModel: DiscussionViewModel,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by discussionViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        discussionViewModel.getDiscussionByReviewId(reviewId = reviewId)
    }

    if (state.discussion == null) {
        Text(text = stringResource(R.string.discusion_no_encontrada))
    } else {
        DiscussionScreenContent(
            discussion = state.discussion!!,
            isReviewLiked = state.isReviewLiked,
            isReviewShared = state.isReviewShared,
            likedCommentIds = state.likedCommentIds,
            replyingToCommentId = state.replyingToCommentId,
            onBackClick = onBackClick,
            onReviewLikeClick = { discussionViewModel.darQuitarLikeResena() },
            onReviewShareClick = { discussionViewModel.compartirQuitarResena() },
            onCommentLikeClick = { discussionViewModel.darQuitarLikeComentario(commentId = it) },
            onCommentReplyClick = { discussionViewModel.responderComentario(commentId = it) },
            modifier = modifier,
        )
    }
}

@Composable
fun DiscussionScreenContent(
    discussion: DiscussionUi,
    isReviewLiked: Boolean,
    isReviewShared: Boolean,
    likedCommentIds: Set<String>,
    replyingToCommentId: String?,
    onBackClick: () -> Unit,
    onReviewLikeClick: () -> Unit,
    onReviewShareClick: () -> Unit,
    onCommentLikeClick: (String) -> Unit,
    onCommentReplyClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DiscussionHeader(
                commentsCount = discussion.review.commentsCount,
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 24.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 18.dp)
            ) {
                item {
                    DiscussionReviewCard(
                        review = discussion.review,
                        isLiked = isReviewLiked,
                        isShared = isReviewShared,
                        onLikeClick = onReviewLikeClick,
                        onShareClick = onReviewShareClick,
                    )
                }

                item {
                    CommentsDivider(
                        modifier = Modifier.padding(top = 20.dp, bottom = 12.dp)
                    )
                }

                items(discussion.comments, key = { it.id }) { comment ->
                    ThreadCommentItem(
                        comment = comment,
                        isLiked = comment.id in likedCommentIds,
                        isReplying = replyingToCommentId == comment.id,
                        onLikeClick = { onCommentLikeClick(comment.id) },
                        onReplyClick = { onCommentReplyClick(comment.id) },
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun DiscussionScreenPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        DiscussionScreenContent(
            discussion = LocalDiscussionProvider.discussions.first(),
            isReviewLiked = false,
            isReviewShared = false,
            likedCommentIds = emptySet(),
            replyingToCommentId = null,
            onBackClick = {},
            onReviewLikeClick = {},
            onReviewShareClick = {},
            onCommentLikeClick = {},
            onCommentReplyClick = {},
        )
    }
}
