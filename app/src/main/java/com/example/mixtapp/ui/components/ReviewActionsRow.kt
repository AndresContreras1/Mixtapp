package com.example.mixtapp.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun ReviewActionsRow(
    likes: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    commentsLabel: String,
    onCommentsClick: (() -> Unit)?,
    isShared: Boolean,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier.size(16.dp),
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(R.string.like_review),
                tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                modifier = Modifier.size(16.dp),
            )
        }
        Text(
            text = (likes + if (isLiked) 1 else 0).toString(),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.width(8.dp))

        if (onCommentsClick == null) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                modifier = Modifier.size(15.dp),
            )
        } else {
            IconButton(
                onClick = onCommentsClick,
                modifier = Modifier.size(15.dp),
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = stringResource(R.string.open_discussion),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
                    modifier = Modifier.size(15.dp),
                )
            }
        }
        Text(
            text = commentsLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f),
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(
            onClick = onShareClick,
            modifier = Modifier.size(18.dp),
        ) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = stringResource(R.string.share_review),
                tint = if (isShared) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}


@Composable
@Preview
fun ReviewActionsRowPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        ReviewActionsRow(
            likes = 24,
            isLiked = false,
            onLikeClick = {},
            commentsLabel = "4",
            onCommentsClick = {},
            isShared = false,
            onShareClick = {}
        )
    }
}
