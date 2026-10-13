package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
        IconButton(
            onClick = onLikeClick,
            modifier = Modifier.size(12.dp),
        ) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(R.string.like_comment),
                tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f),
                modifier = Modifier.size(12.dp),
            )
        }
        Text(
            text = likes.toString(),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f),
            fontSize = 10.sp,
        )

        Spacer(modifier = Modifier.width(18.dp))

        // Alto fijo para que el boton no agrande la fila de acciones
        TextButton(
            onClick = onReplyClick,
            modifier = Modifier.height(24.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = if (isReplying) {
                    stringResource(R.string.replying)
                } else {
                    stringResource(R.string.reply)
                },
                color = if (isReplying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.62f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
            )
        }
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
