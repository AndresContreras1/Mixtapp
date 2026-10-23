package com.example.mixtapp.ui.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
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

// El corazon con el contador: lleno si ya le di like. Lo usan las resenas y los comentarios
@Composable
fun LikeButton(
    likes: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    // Que se marca con like: la resena o el comentario
    @StringRes contentDescriptionRes: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onLikeClick) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(if (isLiked) R.string.quitar_like else contentDescriptionRes),
                tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp),
            )
        }
        Text(
            text = stringResource(R.string.cantidad_likes, likes),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LikeButtonPreview() {
    MixtappTheme(darkTheme = true) {
        LikeButton(
            likes = 24,
            isLiked = true,
            onLikeClick = {},
            contentDescriptionRes = R.string.like_review,
        )
    }
}
