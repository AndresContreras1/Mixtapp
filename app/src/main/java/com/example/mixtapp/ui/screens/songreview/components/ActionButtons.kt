package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R

@Composable
fun ActionButtons(
    isSaved: Boolean,
    isLiked: Boolean,
    onSaveClick: () -> Unit,
    onLikeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AccionDelAlbum(
            texto = stringResource(if (isSaved) R.string.saved else R.string.save),
            icono = Icons.Default.Check,
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            borde = null,
            onClick = onSaveClick,
            modifier = Modifier.weight(1f)
        )

        AccionDelAlbum(
            texto = stringResource(if (isLiked) R.string.liked else R.string.like),
            icono = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentColor = if (isLiked) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            containerColor = Color.Transparent,
            borde = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)),
            onClick = onLikeClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AccionDelAlbum(
    texto: String,
    icono: ImageVector,
    contentColor: Color,
    containerColor: Color,
    borde: BorderStroke?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        border = borde,
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = texto)
    }
}
