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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.ui.screens.discussion.components.CommentsDivider
import com.example.mixtapp.ui.screens.discussion.components.DiscussionHeader
import com.example.mixtapp.ui.screens.discussion.components.DiscussionReviewCard
import com.example.mixtapp.ui.screens.discussion.components.ThreadCommentItem
import com.example.mixtapp.ui.screens.discussion.model.DiscussionUi
import com.example.mixtapp.ui.theme.DeepBackground

@Composable
fun DiscussionScreen(
    discussion: DiscussionUi,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isReviewLiked by rememberSaveable(discussion.review.id) {
        mutableStateOf(discussion.review.isLiked)
    }
    var isReviewShared by rememberSaveable(discussion.review.id) {
        mutableStateOf(discussion.review.isShared)
    }
    var likedCommentIds by rememberSaveable(discussion.review.id) {
        mutableStateOf(discussion.comments.filter { it.isLiked }.map { it.id }.toSet())
    }
    var replyingToCommentId by rememberSaveable { mutableStateOf<String?>(null) }

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
                        onLikeClick = { isReviewLiked = !isReviewLiked },
                        onShareClick = { isReviewShared = !isReviewShared },
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
                        onLikeClick = {
                            likedCommentIds = if (comment.id in likedCommentIds) {
                                likedCommentIds - comment.id
                            } else {
                                likedCommentIds + comment.id
                            }
                        },
                        onReplyClick = { replyingToCommentId = comment.id },
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun DiscussionScreenPreview() {
    DiscussionScreen(
        discussion = LocalDiscussionProvider.discussion,
        onBackClick = {}
    )
}
