package com.example.mixtapp.ui.screens.resumen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.theme.Black
import com.example.mixtapp.ui.theme.Coral
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.White
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.numeroQueCuenta

// La primera tarjeta de Tu año, con el degradado mas llamativo: arriba la reseña con mas me gusta
// (lo que califique, su texto y sus me gusta) y abajo cuantas reseñas escribi y mi promedio de
// estrellas. Sin reseñas solo queda la invitacion a calificar
@Composable
fun TarjetaDeResenas(
    totalResenas: Int,
    promedio: Double,
    // null si ninguna de mis reseñas tiene me gusta
    masQuerida: ReviewUi?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(32.dp))
            .background(Brush.linearGradient(listOf(Coral, Magenta)))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Outlined.RateReview,
            contentDescription = null,
            tint = White.copy(alpha = 0.9f),
            modifier = Modifier.size(36.dp),
        )

        Text(
            text = stringResource(R.string.resumen_resenas_titulo),
            color = White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 10.dp),
        )

        if (masQuerida != null) {
            Spacer(modifier = Modifier.height(18.dp))

            // La reseña mas querida, en un recuadro oscuro translucido
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Black.copy(alpha = 0.25f))
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.resumen_mas_querida),
                    color = White.copy(alpha = 0.85f),
                    style = MaterialTheme.typography.labelLarge,
                )

                Row(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ItemAsyncImage(
                        imagenUrl = masQuerida.item.imagenUrl,
                        titulo = masQuerida.item.titulo,
                        esquinas = 14.dp,
                        modifier = Modifier.size(72.dp),
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = masQuerida.item.titulo,
                            color = White,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Favorite,
                                contentDescription = null,
                                tint = White,
                                modifier = Modifier.size(16.dp),
                            )
                            Text(
                                text = stringResource(R.string.resumen_me_gusta, masQuerida.likes),
                                color = White,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(start = 4.dp),
                            )
                        }
                    }
                }

                if (masQuerida.content.isNotEmpty()) {
                    Text(
                        text = "“${masQuerida.content}”",
                        color = White,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // El total cuenta desde 0 al aparecer la tarjeta
        Text(
            text = numeroQueCuenta(valor = totalResenas).toString(),
            color = White,
            style = MaterialTheme.typography.displayLarge,
        )

        Text(
            text = if (totalResenas == 0) {
                stringResource(R.string.resumen_sin_resenas)
            } else {
                stringResource(R.string.resumen_resenas_y_promedio, promedio.toString())
            },
            color = White.copy(alpha = 0.92f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview(showBackground = true, heightDp = 640)
fun TarjetaDeResenasPreview() {
    MixtappTheme(darkTheme = true) {
        TarjetaDeResenas(
            totalResenas = 12,
            promedio = 4.2,
            masQuerida = LocalPreviewProvider.review.copy(likes = 9),
        )
    }
}
