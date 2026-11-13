package com.example.mixtapp.ui.screens.mixtapedetail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemAsyncImage

// Un item de la mixtape: su portada, el titulo y el subtitulo. Tocarlo abre su detalle; si la
// mixtape es mia, la X lo quita
@Composable
fun MixtapeItemRow(
    item: ItemResumenUi,
    puedeQuitar: Boolean,
    onClick: () -> Unit,
    onQuitarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
            .clickable { onClick() }
            .testTag("itemMixtape_${item.id}")
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemAsyncImage(
            imagenUrl = item.imagenUrl,
            titulo = item.titulo,
            esquinas = 12.dp,
            modifier = Modifier.size(56.dp),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.titulo,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = item.subtitulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        if (puedeQuitar) {
            IconButton(
                onClick = onQuitarClick,
                modifier = Modifier.testTag("btnQuitar_${item.id}"),
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.quitar_de_mixtape),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun MixtapeItemRowPreview() {
    MixtappTheme(darkTheme = true) {
        MixtapeItemRow(
            item = LocalPreviewProvider.fromZero.toItemResumenUi(),
            puedeQuitar = true,
            onClick = {},
            onQuitarClick = {},
        )
    }
}
