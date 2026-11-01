package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppChip
import com.example.mixtapp.ui.utils.BackButton
import com.example.mixtapp.ui.utils.ItemAsyncImage

// El encabezado de un item: la flecha, la imagen grande, el anio y los generos en chips y el
// titulo. Lo usan el detalle y los datos curiosos, asi las dos pantallas se ven iguales arriba
@Composable
fun ItemHeader(
    item: ItemUi,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // Los artistas no tienen anio, y si el subgenero es el mismo genero no se repite
    val datos = listOf(item.anio, item.genero, item.subgenero)
        .filter { it.isNotEmpty() }
        .distinct()

    Column(modifier = modifier.fillMaxWidth()) {
        BackButton(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        ItemAsyncImage(
            imagenUrl = item.imagenUrl,
            titulo = item.titulo,
            esquinas = 28.dp,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
        )

        Spacer(modifier = Modifier.height(20.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            items(datos) { dato ->
                // Solo muestran el dato: no filtran nada
                AppChip(text = dato, isSelected = false, onClick = {})
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = item.titulo,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("tituloItem"),
        )

        Text(
            text = item.subtitulo,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
        )

        // Solo las canciones traen el artista aparte del album
        if (item.detalle.isNotEmpty()) {
            Text(
                text = item.detalle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ItemHeaderPreview() {
    MixtappTheme(darkTheme = true) {
        ItemHeader(
            item = LocalPreviewProvider.emptinessMachine,
            onBackClick = {},
        )
    }
}
