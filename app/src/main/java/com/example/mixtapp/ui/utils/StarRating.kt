package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// La calificacion es de 0 a 5 estrellas, como pide el cliente
private const val MAX_ESTRELLAS = 5

// Las cinco estrellas: llenas hasta la calificacion y el contorno en las demas
@Composable
fun StarRating(
    // Cuantas estrellas se pintan llenas
    rating: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 18.dp,
    // null cuando la calificacion solo se muestra y no se puede tocar
    onRatingChange: ((Int) -> Unit)? = null,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(starSize / 4)
    ) {
        repeat(MAX_ESTRELLAS) { index ->
            val starValue = index + 1
            val isFilled = starValue <= rating

            val descripcion = when {
                onRatingChange == null && isFilled -> stringResource(R.string.estrella_llena)
                onRatingChange == null -> stringResource(R.string.estrella_vacia)
                starValue == 1 -> stringResource(R.string.una_estrella)
                else -> stringResource(R.string.star_rating_format, starValue)
            }
            val tinte = if (isFilled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

            // Si solo se muestra es un icono; si se puede calificar, cada estrella es un boton
            if (onRatingChange == null) {
                Icon(
                    imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.StarOutline,
                    contentDescription = descripcion,
                    tint = tinte,
                    modifier = Modifier.size(starSize)
                )
            } else {
                IconButton(
                    onClick = { onRatingChange(starValue) },
                    modifier = Modifier.size(starSize + 8.dp)
                ) {
                    Icon(
                        imageVector = if (isFilled) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = descripcion,
                        tint = tinte,
                        modifier = Modifier.size(starSize)
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun StarRatingPreview() {
    MixtappTheme(darkTheme = true) {
        StarRating(rating = 3, starSize = 28.dp, onRatingChange = {})
    }
}
