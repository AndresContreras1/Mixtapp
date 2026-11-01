package com.example.mixtapp.ui.screens.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemCard

// Cuantos guardados caben en cada fila de la cuadricula
const val GUARDADOS_POR_FILA = 3

// Una fila de la cuadricula de guardados. Cada item ocupa la misma parte del ancho; si la ultima
// fila trae menos, los huecos guardan su espacio para que no se estiren
@Composable
fun SavedItemsRow(
    items: List<ItemResumenUi>,
    onItemClick: (ItemResumenUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items.forEach { item ->
            // El Box reparte el ancho y la tarjeta conserva su imagen cuadrada en el centro
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.TopCenter,
            ) {
                ItemCard(
                    titulo = item.titulo,
                    subtitulo = item.subtitulo,
                    detalle = "",
                    imagenUrl = item.imagenUrl,
                    onClick = { onItemClick(item) },
                    tamano = 100.dp,
                )
            }
        }

        repeat(GUARDADOS_POR_FILA - items.size) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SavedItemsRowPreview() {
    MixtappTheme(darkTheme = true) {
        SavedItemsRow(
            items = LocalPreviewProvider.favoritos.take(2),
            onItemClick = {},
        )
    }
}
