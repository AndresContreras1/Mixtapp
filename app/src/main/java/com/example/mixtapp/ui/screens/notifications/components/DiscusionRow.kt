package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.foundation.background
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
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.ProfileAsyncImage
import com.example.mixtapp.ui.utils.TiempoTranscurrido

// Una resena nueva de alguien que sigo, con la estructura de las notificaciones: arriba "Nueva
// discusion"; la foto y el nombre (tocarlos abre su perfil), lo resenado resaltado con su miniatura,
// cuanto hace y cuantos comentarios lleva, con su etiqueta. Tocar la fila abre la discusion
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
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.5f))
            .clickable { onClick() }
            .testTag("discusion_${review.id}")
            .padding(horizontal = 12.dp, vertical = 10.dp),
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
                text = stringResource(R.string.evento_discusion),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
            )
            Text(
                text = review.author,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .clickable { onAuthorClick() }
                    .testTag("autor_discusion_${review.id}"),
            )
            // Lo resenado, resaltado en rosado
            Text(
                text = review.item.titulo,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Cuanto hace y cuantos comentarios lleva, con su etiqueta para que se entienda el numero
            Row(verticalAlignment = Alignment.CenterVertically) {
                TiempoTranscurrido(timeAgo = review.timeAgo)
                Text(
                    text = "  ·  " + stringResource(R.string.comentarios_count, review.comments),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        // La miniatura de lo resenado
        ItemAsyncImage(
            imagenUrl = review.item.imagenUrl,
            titulo = review.item.titulo,
            esquinas = 10.dp,
            modifier = Modifier.size(46.dp),
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
