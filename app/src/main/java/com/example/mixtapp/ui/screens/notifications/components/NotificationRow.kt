package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.NOTIFICACION_COMENTARIO
import com.example.mixtapp.data.model.NOTIFICACION_LIKE
import com.example.mixtapp.data.model.NOTIFICACION_RESPUESTA
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.ProfileAsyncImage

// Una notificacion: la foto de quien la genero, el texto con su nombre en negrita, cuanto hace,
// la miniatura de lo resenado y un punto mientras no este leida. Las no leidas resaltan mas
@Composable
fun NotificationRow(
    notification: NotificationUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val texto = textoDeNotificacion(notification = notification)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                MaterialTheme.colorScheme.surfaceContainer.copy(alpha = if (notification.isRead) 0.35f else 0.8f)
            )
            .clickable { onClick() }
            .testTag("notificacion_${notification.id}")
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileAsyncImage(
            profileImage = notification.actorImage,
            contentDescription = stringResource(R.string.foto_perfil),
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildAnnotatedString {
                    // Todos los textos empiezan con el nombre de quien la genero: ese pedazo va en negrita
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(notification.actorName)
                    }
                    append(texto.removePrefix(notification.actorName))
                },
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                // Hace menos de un minuto el mapeo deja el texto vacio
                text = notification.timeAgo.ifEmpty { stringResource(R.string.ahora) },
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelSmall,
            )
        }

        // Un seguidor nuevo no trae miniatura
        if (notification.itemImage.isNotEmpty()) {
            Spacer(modifier = Modifier.width(10.dp))

            ItemAsyncImage(
                imagenUrl = notification.itemImage,
                titulo = notification.itemTitle,
                esquinas = 10.dp,
                modifier = Modifier.size(44.dp)
            )
        }

        if (!notification.isRead) {
            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

// El texto segun el tipo. Si la resena no tiene titulo se usa la variante sin "de ..."
@Composable
private fun textoDeNotificacion(notification: NotificationUi): String {
    val nombre = notification.actorName
    val titulo = notification.itemTitle

    return when (notification.tipo) {
        NOTIFICACION_LIKE -> if (titulo.isEmpty()) {
            stringResource(R.string.notificacion_like_sin_item, nombre)
        } else {
            stringResource(R.string.notificacion_like, nombre, titulo)
        }
        NOTIFICACION_COMENTARIO -> if (titulo.isEmpty()) {
            stringResource(R.string.notificacion_comentario_sin_item, nombre)
        } else {
            stringResource(R.string.notificacion_comentario, nombre, titulo)
        }
        NOTIFICACION_RESPUESTA -> stringResource(R.string.notificacion_respuesta, nombre)
        // NOTIFICACION_SEGUIDOR
        else -> stringResource(R.string.notificacion_seguidor, nombre)
    }
}

@Composable
@Preview(showBackground = true)
fun NotificationRowPreview() {
    MixtappTheme(darkTheme = true) {
        NotificationRow(
            notification = LocalPreviewProvider.notifications.first(),
            onClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
fun NotificationRowSeguidorPreview() {
    MixtappTheme(darkTheme = true) {
        NotificationRow(
            notification = LocalPreviewProvider.notifications.last(),
            onClick = {},
        )
    }
}
