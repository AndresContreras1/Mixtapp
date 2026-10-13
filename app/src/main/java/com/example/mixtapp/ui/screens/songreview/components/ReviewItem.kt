package com.example.mixtapp.ui.screens.songreview.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.SongReviewItemUi
import com.example.mixtapp.ui.utils.ProfileAsyncImage
import com.example.mixtapp.ui.utils.StarRating

// La resena del usuario de la app (esMia) se marca y trae los botones de editar y eliminar
@Composable
fun ReviewItem(
    review: SongReviewItemUi,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.outlinedCardColors(containerColor = Color.Transparent),
        border = BorderStroke(
            1.dp,
            if (review.esMia) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onAuthorClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    ProfileAsyncImage(
                        profileImage = review.authorImage,
                        contentDescription = stringResource(R.string.foto_perfil),
                        modifier = Modifier.size(40.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (review.esMia) "${review.author} ${stringResource(R.string.marca_propia)}" else review.author,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                    StarRating(
                        rating = review.rating,
                        // Solo se dibujan las estrellas obtenidas, sin ranuras vacias
                        starCount = review.rating,
                        starSize = 12.dp,
                        spacing = 0.dp,
                        filledTint = MaterialTheme.colorScheme.primary,
                        emptyTint = MaterialTheme.colorScheme.primary,
                        emptyIcon = Icons.Filled.Star,
                        onRatingChange = null
                    )
                }
                Text(review.fecha, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f), fontSize = 10.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = review.content,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            if (review.esMia) {
                Row {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(R.string.editar_resena),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteClick) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.eliminar_resena),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}
