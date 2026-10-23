package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
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

// Like, comentarios y compartir de una resena. Sin onCommentsClick el icono de comentarios solo
// acompana el total, como en la misma discusion
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
        LikeButton(
            likes = likes,
            isLiked = isLiked,
            onLikeClick = onLikeClick,
            contentDescriptionRes = R.string.like_review,
        )

        if (onCommentsClick == null) {
            Icon(
                imageVector = Icons.Outlined.ChatBubbleOutline,
                contentDescription = stringResource(R.string.comments),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        } else {
            IconButton(onClick = onCommentsClick) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = stringResource(R.string.open_discussion),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp),
                )
            }
        }
        Text(
            text = commentsLabel,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onShareClick) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = stringResource(R.string.share_review),
                tint = if (isShared) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}


@Composable
@Preview(showBackground = true)
fun ReviewActionsRowPreview() {
    MixtappTheme(darkTheme = true) {
        ReviewActionsRow(
            likes = 24,
            isLiked = false,
            onLikeClick = {},
            commentsLabel = stringResource(R.string.comments_count, 4),
            onCommentsClick = {},
            isShared = false,
            onShareClick = {}
        )
    }
}
