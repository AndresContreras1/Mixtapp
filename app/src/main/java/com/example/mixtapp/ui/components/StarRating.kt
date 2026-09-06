package com.example.mixtapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.PrimaryPink
import com.example.mixtapp.ui.theme.TextPink

/**
 * Fila de estrellas de una calificacion. Antes estaba copiada en nueve componentes
 * distintos, cada uno con su propio tamano y sus propios colores.
 *
 * Todos los parametros son obligatorios salvo [modifier]: asi ninguna pantalla puede
 * olvidarse de pasar el callback y quedarse con estrellas que no responden.
 */
@Composable
fun StarRating(
    // Cuantas estrellas se pintan llenas
    rating: Int,
    // Cuantas ranuras se dibujan en total. Normalmente 5, que es el maximo del cliente
    starCount: Int,
    starSize: Dp,
    spacing: Dp,
    filledTint: Color,
    emptyTint: Color,
    // Icono de la ranura vacia: la misma estrella atenuada, o el contorno
    emptyIcon: ImageVector,
    // null cuando la calificacion solo se muestra y no se puede tocar
    onRatingChange: ((Int) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        repeat(starCount) { index ->
            val starValue = index + 1
            val isFilled = starValue <= rating

            Icon(
                imageVector = if (isFilled) Icons.Filled.Star else emptyIcon,
                // Solo se anuncia cuando se puede tocar; si no, TalkBack leeria cinco veces
                contentDescription = if (onRatingChange == null) {
                    null
                } else {
                    stringResource(R.string.star_rating_format, starValue)
                },
                tint = if (isFilled) filledTint else emptyTint,
                modifier = Modifier
                    .size(starSize)
                    .then(
                        if (onRatingChange == null) {
                            Modifier
                        } else {
                            Modifier.clickable { onRatingChange(starValue) }
                        }
                    )
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun StarRatingPreview() {
    StarRating(
        rating = 3,
        starCount = 5,
        starSize = 24.dp,
        spacing = 6.dp,
        filledTint = PrimaryPink,
        emptyTint = TextPink.copy(alpha = 0.32f),
        emptyIcon = Icons.Filled.Star,
        onRatingChange = null
    )
}
