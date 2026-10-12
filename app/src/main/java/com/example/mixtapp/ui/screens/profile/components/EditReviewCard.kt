package com.example.mixtapp.ui.screens.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.model.MAX_REVIEW_LENGTH
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.displayFontFamily
import com.example.mixtapp.ui.utils.AlbumAsyncImage
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.AppTextArea
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.StarRating

// Formulario para editar una resena dentro del mismo perfil
@Composable
fun EditReviewCard(
    review: MyReviewUi,
    rating: Int,
    texto: String,
    guardando: Boolean,
    errorMessageRes: Int?,
    onRatingChange: (Int) -> Unit,
    onTextoChange: (String) -> Unit,
    onGuardarClick: () -> Unit,
    onCancelarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AlbumAsyncImage(
                    cover = review.album.cover,
                    contentDescription = stringResource(R.string.album_cover, review.album.title),
                    alpha = 1f,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(10.dp))
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = review.album.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = displayFontFamily,
                        maxLines = 1
                    )
                    Text(
                        text = review.album.artist,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            StarRating(
                rating = rating,
                starCount = 5,
                starSize = 28.dp,
                spacing = 6.dp,
                filledTint = MaterialTheme.colorScheme.primary,
                emptyTint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                emptyIcon = Icons.Filled.Star,
                onRatingChange = onRatingChange
            )

            Spacer(modifier = Modifier.height(14.dp))

            AppTextArea(
                value = texto,
                onValueChange = onTextoChange,
                label = stringResource(R.string.label_resena),
                placeholder = stringResource(R.string.review_placeholder),
                maxLength = MAX_REVIEW_LENGTH
            )

            ErrorMessage(
                messageRes = errorMessageRes,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onCancelarClick) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.64f),
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                AppButton(
                    texto = stringResource(R.string.save),
                    onClick = onGuardarClick,
                    cargando = guardando,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.height(40.dp)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EditReviewCardPreview() {
    val review = LocalMyReviewsProvider.reviews.first()

    MixtappTheme(darkTheme = true) {
        EditReviewCard(
            review = review,
            rating = review.rating,
            texto = review.excerpt,
            guardando = false,
            errorMessageRes = null,
            onRatingChange = {},
            onTextoChange = {},
            onGuardarClick = {},
            onCancelarClick = {}
        )
    }
}
