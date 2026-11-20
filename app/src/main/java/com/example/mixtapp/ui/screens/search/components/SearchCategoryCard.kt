package com.example.mixtapp.ui.screens.search.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.data.model.CATEGORIA_FECHA_LANZAMIENTO
import com.example.mixtapp.data.model.CATEGORIA_MAS_POPULARES
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.ui.theme.Black
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.Violeta
import com.example.mixtapp.ui.theme.White
import com.example.mixtapp.ui.utils.ItemAsyncImage

// Una tarjeta de Generos: la imagen de fondo con el mismo degradado oscuro abajo en todas y el
// nombre en blanco y con peso, para que se lea aunque la portada tenga mucho detalle
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

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(0.3f to Color.Transparent, 1f to Black.copy(alpha = 0.85f)))
            )

            Text(
                text = titulo,
                color = White,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
            )
        }
    }
}

// Las tres formas de explorar en una sola fila pareja: cada una con su icono, porque son criterios y
// no musica (una portada no explica "Mas populares"). Las tres llevan el mismo degradado de la marca
@Composable
fun CriteriosDeBusqueda(
    criterios: List<SearchCategoryUi>,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        criterios.forEach { criterio ->
            val icono = when (criterio.id) {
                CATEGORIA_FECHA_LANZAMIENTO -> Icons.Outlined.CalendarMonth
                CATEGORIA_MAS_POPULARES -> Icons.AutoMirrored.Filled.TrendingUp
                else -> Icons.Outlined.Star
            }

            Card(
                onClick = { onCategoryClick(criterio.id) },
                modifier = Modifier
                    .weight(1f)
                    .height(104.dp)
                    .testTag("categoria_${criterio.id}"),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Brush.linearGradient(listOf(Magenta, Violeta)))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    Icon(
                        imageVector = icono,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.size(28.dp),
                    )
                    Text(
                        text = stringResource(criterio.titleRes),
                        color = White,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 2,
                    )
                }
            }
        }
    }
}

// Un genero en una tarjeta horizontal a todo el ancho. La portada va en gris con el degradado rosado y
// morado de la marca encima, asi todas se ven parejas aunque las portadas tengan colores distintos.
// El nombre va grande a la izquierda
@Composable
fun TarjetaDeGenero(
    genero: SearchCategoryUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val titulo = stringResource(genero.titleRes)
    val gris = ColorMatrix().apply { setToSaturation(0f) }

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(92.dp),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(genero.imagenUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = stringResource(R.string.portada_de, titulo),
                contentScale = ContentScale.Crop,
                colorFilter = ColorFilter.colorMatrix(gris),
                modifier = Modifier.fillMaxSize(),
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            listOf(Magenta.copy(alpha = 0.92f), Violeta.copy(alpha = 0.7f), Violeta.copy(alpha = 0.35f))
                        )
                    )
            )

            Text(
                text = titulo,
                color = White,
                style = MaterialTheme.typography.headlineSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 22.dp),
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
