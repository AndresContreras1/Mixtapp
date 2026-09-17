package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.StarRating
import com.example.mixtapp.ui.theme.displayFontFamily
import kotlin.math.roundToInt

@Composable
fun ReleaseStats(
    rating: Double,
    ratingCount: String,
    recommendRate: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Color.Transparent,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            StatItem(
                value = stringResource(R.string.rating_promedio, rating),
                label = null,
                estrellas = rating.roundToInt(),
                modifier = Modifier.weight(1f)
            )
            VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), modifier = Modifier.height(30.dp))
            StatItem(value = ratingCount, label = stringResource(R.string.ratings_label), modifier = Modifier.weight(1f))
            VerticalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f), modifier = Modifier.height(30.dp))
            StatItem(
                value = stringResource(R.string.recomendacion_porcentaje, recommendRate),
                label = stringResource(R.string.recommend_label),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatItem(value: String, label: String?, estrellas: Int? = null, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = MaterialTheme.colorScheme.primary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = displayFontFamily
        )
        if (estrellas != null) {
            StarRating(
                rating = estrellas,
                starCount = 5,
                starSize = 12.dp,
                spacing = 0.dp,
                filledTint = MaterialTheme.colorScheme.primary,
                emptyTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                emptyIcon = Icons.Filled.Star,
                onRatingChange = null
            )
        }
        if (label != null) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                fontSize = 12.sp
            )
        }
    }
}
