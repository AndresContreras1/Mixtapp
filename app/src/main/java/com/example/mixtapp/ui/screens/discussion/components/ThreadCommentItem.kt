package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.ui.theme.displayFontFamily

@Composable
fun ThreadCommentItem(
    comment: DiscussionCommentUi,
    isLiked: Boolean,
    isReplying: Boolean,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (comment.isReply) 38.dp else 0.dp,
                top = 7.dp,
                bottom = 5.dp,
            )
    ) {
        CommentAvatarColumn(
            initials = comment.initials,
            isReply = comment.isReply,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.author,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.58f),
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontFamily = displayFontFamily,
                    fontWeight = FontWeight.Black,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = comment.timeAgo,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.55f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = comment.content,
                modifier = Modifier.padding(top = 6.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
                fontSize = 10.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
            )

            CommentActionsRow(
                likes = comment.likes,
                isLiked = isLiked,
                isReplying = isReplying,
                onLikeClick = onLikeClick,
                onReplyClick = onReplyClick,
                modifier = Modifier.padding(top = 6.dp),
            )
        }
    }
}
