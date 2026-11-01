package com.example.mixtapp.ui.utils

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.theme.MixtappTheme

// La tarjeta de resena de toda la app: el inicio, el detalle, los perfiles, Mis resenas, Siguiendo y
// la discusion. Arriba quien la escribio y su calificacion, despues lo que califico, el texto y las
// acciones. Cada pantalla le pasa sus funciones (state hoisting)
@Composable
fun ReviewCard(
    review: ReviewUi,
    onAuthorClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    // En el detalle de un item no se repite el item; en las demas listas si
    mostrarItem: Boolean = true,
    onItemClick: (ItemResumenUi) -> Unit = {},
    // Solo para mis resenas en mi perfil y en Mis resenas: null en las demas
    onEditClick: ((ReviewUi) -> Unit)? = null,
    onDeleteClick: ((String) -> Unit)? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Quien la escribio: tocarlo abre su perfil
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
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.author,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        // Hace menos de un minuto el mapeo deja el texto vacio
                        text = review.timeAgo.ifEmpty { stringResource(R.string.ahora) },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }

                StarRating(rating = review.rating, starSize = 14.dp)
            }

            // Lo que califico: tocarlo abre su detalle
            if (mostrarItem) {
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(review.item) },
                ) {
                    ItemAsyncImage(
                        imagenUrl = review.item.imagenUrl,
                        titulo = review.item.titulo,
                        esquinas = 12.dp,
                        modifier = Modifier.size(56.dp),
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = review.item.titulo,
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = review.item.subtitulo,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }

            if (review.content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = review.content,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                LikeButton(
                    likesText = review.likesText,
                    isLiked = review.isLiked,
                    onLikeClick = { onLikeClick(review.id) },
                    contentDescriptionRes = R.string.like_review,
                    modifier = Modifier.testTag("like_${review.id}"),
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

@Composable
@Preview(showBackground = true)
fun ReviewCardPreview() {
    MixtappTheme(darkTheme = true) {
        ReviewCard(
            review = LocalPreviewProvider.review,
            onAuthorClick = {},
            onLikeClick = {},
            onCommentsClick = {},
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
            onEditClick = {},
            onDeleteClick = {},
        )
    }
}
