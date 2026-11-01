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
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// El corazon con su contador. likesText llega listo del mapeo (14K)
@Composable
fun LikeButton(
    likesText: String,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    // Que se marca con like: la resena, el comentario o el item
    @StringRes contentDescriptionRes: Int,
    modifier: Modifier = Modifier,
    // Para las pruebas e2e: el corazon se toca y el contador se lee por separado
    botonModifier: Modifier = Modifier,
    contadorModifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onLikeClick, modifier = botonModifier) {
            Icon(
                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                contentDescription = stringResource(if (isLiked) R.string.quitar_like else contentDescriptionRes),
                tint = if (isLiked) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp),
            )
        }
        Text(
            text = likesText,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            modifier = contadorModifier,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LikeButtonPreview() {
    MixtappTheme(darkTheme = true) {
        LikeButton(
            likesText = "14K",
            isLiked = true,
            onLikeClick = {},
            contentDescriptionRes = R.string.like_review,
        )
    }
}
