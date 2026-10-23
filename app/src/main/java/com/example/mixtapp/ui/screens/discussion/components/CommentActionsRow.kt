package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.LikeButton

@Composable
fun CommentActionsRow(
    likes: Int,
    isLiked: Boolean,
    isReplying: Boolean,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LikeButton(
            likes = likes,
            isLiked = isLiked,
            onLikeClick = onLikeClick,
            contentDescriptionRes = R.string.like_comment,
        )

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = if (isReplying) {
                stringResource(R.string.replying)
            } else {
                stringResource(R.string.reply)
            },
            color = if (isReplying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onReplyClick() },
        )
    }
}

@Composable
@Preview(showBackground = true)
fun CommentActionsRowPreview() {
    MixtappTheme(darkTheme = true) {
        CommentActionsRow(
            likes = 12,
            isLiked = true,
            isReplying = false,
            onLikeClick = {},
            onReplyClick = {},
        )
    }
}
