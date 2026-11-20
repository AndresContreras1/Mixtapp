package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.StarRating
import com.example.mixtapp.ui.utils.TarjetaTranslucida

// Las estadisticas del item en una tarjeta, solo las tres que importan: el promedio con sus
// estrellas, cuantas calificaciones tiene y cuantos lo recomiendan. Debajo va de cuantas personas
// sale el porcentaje ("1 de 1 lo recomiendan"), para que un 100 % con una sola resena no parezca mas
// de lo que es. Los me gusta ya se ven en el corazon de las acciones
@Composable
fun ItemStats(
    rating: Double,
    estrellas: Int,
    ratingCount: Int,
    recommendRate: Int,
    recomiendan: Int,
    modifier: Modifier = Modifier,
) {
    TarjetaTranslucida(
        onClick = null,
        modifier = modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                // El promedio se entiende por sus estrellas: no lleva etiqueta
                StatItem(
                    value = stringResource(R.string.rating_promedio, rating),
                    label = null,
                    estrellas = estrellas,
                    modifier = Modifier.weight(1f),
                )
                StatItem(
                    value = ratingCount.toString(),
                    label = stringResource(if (ratingCount == 1) R.string.calificacion_label else R.string.ratings_label),
                    estrellas = null,
                    modifier = Modifier.weight(1f),
                )
                StatItem(
                    value = stringResource(R.string.recomendacion_porcentaje, recommendRate),
                    label = stringResource(R.string.recommend_label),
                    estrellas = null,
                    modifier = Modifier.weight(1f),
                )
            }

            Text(
                text = if (ratingCount == 0) {
                    stringResource(R.string.sin_calificaciones_todavia)
                } else {
                    stringResource(R.string.recomiendan_de, recomiendan, ratingCount)
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )
        }
    }
}

// Un numero grande y debajo su etiqueta; el promedio lleva sus estrellas en vez de la etiqueta
@Composable
private fun StatItem(
    value: String,
    // null en el promedio
    label: String?,
    estrellas: Int?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.headlineMedium,
        )
        if (estrellas != null) {
            StarRating(rating = estrellas, starSize = 14.dp, onRatingChange = null)
        }
        if (label != null) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ItemStatsPreview() {
    MixtappTheme(darkTheme = true) {
        val itemConResenas = LocalPreviewProvider.itemConResenas

        ItemStats(
            rating = itemConResenas.rating,
            estrellas = itemConResenas.estrellas,
            ratingCount = itemConResenas.ratingCount,
            recommendRate = itemConResenas.recommendRate,
            recomiendan = itemConResenas.recomiendan,
        )
    }
}
