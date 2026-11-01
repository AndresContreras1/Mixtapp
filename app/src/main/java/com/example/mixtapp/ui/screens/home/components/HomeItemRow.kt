package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemCard
import com.example.mixtapp.ui.utils.SectionTitle

// Una fila del inicio: el titulo y los items con scroll horizontal. La cancion muestra tres lineas
// (titulo, album y artista) y el album y el artista, dos. Tocar uno abre su detalle
@Composable
fun HomeItemRow(
    titulo: String,
    items: List<ItemUi>,
    onItemClick: (ItemUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SectionTitle(title = titulo)

        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            items(items) { item ->
                ItemCard(
                    titulo = item.titulo,
                    subtitulo = item.subtitulo,
                    detalle = item.detalle,
                    imagenUrl = item.imagenUrl,
                    onClick = { onItemClick(item) },
                    modifier = Modifier.testTag("item_${item.id}"),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeItemRowPreview() {
    MixtappTheme(darkTheme = true) {
        HomeItemRow(
            titulo = stringResource(R.string.canciones_populares),
            items = listOf(LocalPreviewProvider.emptinessMachine, LocalPreviewProvider.emptinessMachine.copy(id = "otra")),
            onItemClick = {},
        )
    }
}
