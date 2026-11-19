package com.example.mixtapp.ui.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.theme.MixtappTheme

// La tarjeta de resena de toda la app: el inicio, el detalle, los perfiles, Mis resenas, Siguiendo y
// la discusion. Siempre con el mismo orden: quien la escribio y cuando; lo que califico con su
// calificacion; el texto, que es lo que mas se lee; y abajo, separadas por una linea, las
// interacciones. Cada pantalla le pasa sus funciones (state hoisting)
@Composable
fun ReviewCard(
    review: ReviewUi,
    onAuthorClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    // Tocar lo que califico abre su detalle; null en el detalle de un item, que no lo repite
    onItemClick: ((ItemResumenUi) -> Unit)?,
    // Solo para mis resenas en mi perfil y en Mis resenas: null en las demas
    onEditClick: ((ReviewUi) -> Unit)?,
    onDeleteClick: ((String) -> Unit)?,
) {
    TarjetaTranslucida(
        onClick = null,
        modifier = modifier
            .fillMaxWidth()
            .testTag("review_${review.id}"),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 1. Quien la escribio y cuando: tocarlo abre su perfil
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAuthorClick(review.authorId) }
                    .testTag("autor_${review.id}"),
            ) {
                ProfileAsyncImage(
                    profileImage = review.authorImage,
                    contentDescription = stringResource(R.string.foto_perfil),
                    size = 36
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.author,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    TiempoTranscurrido(timeAgo = review.timeAgo)
                }

                // En el detalle no va el item: la calificacion sube al lado del autor
                if (onItemClick == null) {
                    Calificacion(rating = review.rating)
                }
            }

            // 2. Lo que califico y su calificacion: tocarlo abre su detalle
            if (onItemClick != null) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    PortadaConTitulo(
                        imagenUrl = review.item.imagenUrl,
                        titulo = review.item.titulo,
                        subtitulo = review.item.subtitulo,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onItemClick(review.item) },
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Calificacion(rating = review.rating)
                }
            }

            // 3. El texto de la resena
            if (review.content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = review.content,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyLarge,
                )
            }

            // 4. Las interacciones, aparte del contenido
            HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                LikeButton(
                    likesText = review.likesText,
                    isLiked = review.isLiked,
                    onLikeClick = { onLikeClick(review.id) },
                    contentDescriptionRes = R.string.like_review,
                    botonModifier = Modifier.testTag("like_${review.id}"),
                    contadorModifier = Modifier.testTag("likes_${review.id}"),
                )

                // Abre la discusion aunque no siga a quien la escribio
                CommentsButton(
                    comments = review.comments,
                    onCommentsClick = { onCommentsClick(review.id) },
                )

                Spacer(modifier = Modifier.weight(1f))

                if (review.isMine && onEditClick != null && onDeleteClick != null) {
                    EditDeleteButtons(
                        onEditClick = { onEditClick(review) },
                        onDeleteClick = { onDeleteClick(review.id) },
                    )
                }
            }
        }
    }
}

// Las estrellas con "4/5" al lado, para que se entienda la calificacion sin contar estrellas
@Composable
private fun Calificacion(
    rating: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
    ) {
        StarRating(rating = rating, starSize = 14.dp, onRatingChange = null)
        Text(
            text = stringResource(R.string.calificacion_sobre_cinco, rating),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ReviewCardPreview() {
    MixtappTheme(darkTheme = true) {
        ReviewCard(
            review = LocalPreviewProvider.review,
            onAuthorClick = {},
            onLikeClick = {},
            onCommentsClick = {},
            onItemClick = {},
            onEditClick = null,
            onDeleteClick = null,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ReviewCardMiaPreview() {
    MixtappTheme(darkTheme = true) {
        ReviewCard(
            review = LocalPreviewProvider.miReview,
            onAuthorClick = {},
            onLikeClick = {},
            onCommentsClick = {},
            onItemClick = {},
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}
