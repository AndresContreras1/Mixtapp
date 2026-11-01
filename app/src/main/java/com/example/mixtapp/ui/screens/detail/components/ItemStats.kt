package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.StarRating

// Las estadisticas del item en una tarjeta: el promedio con sus estrellas, cuantas calificaciones
// tiene, cuantos lo recomiendan y sus me gusta. Todo sale de las resenas y del item en vivo
@Composable
fun ItemStats(
    rating: Double,
    estrellas: Int,
    ratingCount: Int,
    recommendRate: Int,
    likesText: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 16.dp),
        ) {
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
            StatItem(
                value = likesText,
                label = stringResource(R.string.likes_label),
                estrellas = null,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

// Un numero grande y debajo su etiqueta, o las estrellas en el caso del promedio
@Composable
private fun StatItem(
    value: String,
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
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
        )
        if (estrellas != null) {
            StarRating(rating = estrellas, starSize = 11.dp)
        }
        if (label != null) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ItemStatsPreview() {
    val itemConResenas = LocalPreviewProvider.itemConResenas

    MixtappTheme(darkTheme = true) {
        ItemStats(
            rating = itemConResenas.rating,
            estrellas = itemConResenas.estrellas,
            ratingCount = itemConResenas.ratingCount,
            recommendRate = itemConResenas.recommendRate,
            likesText = itemConResenas.item.likesText,
        )
    }
}
