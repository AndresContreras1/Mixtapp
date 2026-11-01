package com.example.mixtapp.ui.screens.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemAsyncImage

// Una tarjeta de Explorar por o de Generos: la imagen de fondo y el nombre sobre una franja
// translucida
@Composable
fun SearchCategoryCard(
    categoria: SearchCategoryUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val titulo = stringResource(categoria.titleRes)

    Card(
        onClick = onClick,
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            ItemAsyncImage(
                imagenUrl = categoria.imagenUrl,
                titulo = titulo,
                modifier = Modifier.fillMaxSize(),
            )

            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.75f))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

// Una fila de la cuadricula: dos tarjetas del mismo ancho. Si la fila trae una sola, el espacio
// vacio la deja a la mitad del ancho como las demas
@Composable
fun SearchCategoryCardsRow(
    categorias: List<SearchCategoryUi>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        categorias.forEach { categoria ->
            SearchCategoryCard(
                categoria = categoria,
                onClick = { onCategoryClick(categoria.id) },
                modifier = Modifier
                    .weight(1f)
                    .testTag("categoria_${categoria.id}"),
            )
        }

        if (categorias.size == 1) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SearchCategoryCardPreview() {
    MixtappTheme(darkTheme = true) {
        SearchCategoryCard(
            categoria = LocalSearchCategoriesProvider.categories.first(),
            onClick = {},
            modifier = Modifier.padding(8.dp),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun SearchCategoryCardsRowPreview() {
    MixtappTheme(darkTheme = true) {
        SearchCategoryCardsRow(
            categorias = LocalSearchCategoriesProvider.categories.take(2),
            onCategoryClick = {},
        )
    }
}
