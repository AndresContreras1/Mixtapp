package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.LikeButton

// Las acciones del item: calificarlo (o editar mi calificacion), darle like y guardarlo en mi
// perfil. El corazon y el marcador cambian cuando llega el item por el listener
@Composable
fun ItemActions(
    // Si ya escribi mi resena, el boton la edita
    yaCalificado: Boolean,
    likesText: String,
    isLiked: Boolean,
    isSaved: Boolean,
    onRateClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppButton(
            texto = stringResource(if (yaCalificado) R.string.editar_calificacion else R.string.calificar),
            onClick = onRateClick,
            modifier = Modifier
                .weight(1f)
                .testTag("btnCalificar"),
        )

        Spacer(modifier = Modifier.width(8.dp))

        LikeButton(
            likesText = likesText,
            isLiked = isLiked,
            onLikeClick = onLikeClick,
            contentDescriptionRes = R.string.like_item,
            modifier = Modifier.testTag("btnLikeItem"),
        )

        // Lo guardado aparece en la pestana Guardados del perfil
        IconButton(
            onClick = onSaveClick,
            modifier = Modifier.testTag("btnGuardar"),
        ) {
            Icon(
                imageVector = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                contentDescription = stringResource(if (isSaved) R.string.quitar_de_guardados else R.string.save),
                tint = if (isSaved) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(26.dp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ItemActionsPreview() {
    val item = LocalPreviewProvider.fromZero

    MixtappTheme(darkTheme = true) {
        ItemActions(
            yaCalificado = false,
            likesText = item.likesText,
            isLiked = item.isLiked,
            isSaved = true,
            onRateClick = {},
            onLikeClick = {},
            onSaveClick = {},
        )
    }
}
