package com.example.mixtapp.ui.screens.mixtapedetail.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.PortadaConTitulo
import com.example.mixtapp.ui.utils.rebote
import com.example.mixtapp.ui.utils.texturaDeTarjeta

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
            .rebote()
            .texturaDeTarjeta(RoundedCornerShape(18.dp))
            .clickable { onClick() }
            .testTag("itemMixtape_${item.id}")
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PortadaConTitulo(
            imagenUrl = item.imagenUrl,
            titulo = item.titulo,
            subtitulo = item.subtitulo,
            modifier = Modifier.weight(1f),
        )

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
