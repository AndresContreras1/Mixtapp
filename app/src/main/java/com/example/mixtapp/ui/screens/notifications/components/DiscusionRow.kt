package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.CommentsButton
import com.example.mixtapp.ui.utils.ProfileAsyncImage

// Una resena nueva de alguien que sigo, pintada como una notificacion: su foto y su nombre (tocarlos
// abre su perfil), sobre que abrio la discusion, cuanto hace y cuantos comentarios lleva. Tocar la
// fila abre la discusion
@Composable
fun DiscusionRow(
    review: ReviewUi,
    onClick: () -> Unit,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
            .clickable { onClick() }
            .testTag("discusion_${review.id}")
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileAsyncImage(
            profileImage = review.authorImage,
            contentDescription = stringResource(R.string.foto_perfil),
            size = 44,
            modifier = Modifier.clickable { onAuthorClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = review.author,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable { onAuthorClick() }
                    .testTag("autor_discusion_${review.id}"),
            )
            Text(
                text = stringResource(R.string.discusion_abierta_sobre, review.item.titulo),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                // Hace menos de un minuto el mapeo deja el texto vacio
                text = review.timeAgo.ifEmpty { stringResource(R.string.ahora) },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // El total de comentarios; tocarlo tambien abre la discusion
        CommentsButton(
            comments = review.comments,
            onCommentsClick = onClick,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun DiscusionRowPreview() {
    MixtappTheme(darkTheme = true) {
        DiscusionRow(
            review = LocalPreviewProvider.review,
            onClick = {},
            onAuthorClick = {},
        )
    }
}
