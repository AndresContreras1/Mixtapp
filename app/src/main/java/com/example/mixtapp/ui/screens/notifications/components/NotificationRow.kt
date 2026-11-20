package com.example.mixtapp.ui.screens.notifications.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material.icons.outlined.Reply
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.mixtapp.data.model.NOTIFICACION_LIKE_COMENTARIO
import com.example.mixtapp.data.model.NOTIFICACION_REACCION_SONANDO
import com.example.mixtapp.data.model.NOTIFICACION_RESPUESTA
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.ProfileAsyncImage
import com.example.mixtapp.ui.utils.TiempoTranscurrido
import com.example.mixtapp.ui.utils.texturaDeTarjeta

// Una notificacion con una estructura fija: arriba que paso ("Respondieron tu comentario") con su
// icono; despues la foto, el nombre en negrita, la accion y lo resenado resaltado; cuanto hace en gris
// y la miniatura. Sin leer va con el punto rosado, el texto con mas peso y mas contraste; al abrirla
// queda neutra (lo trae el listener)
@Composable
fun NotificationRow(
    notification: NotificationUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val sinLeer = !notification.isRead
    val texto = stringResource(
        textoDeNotificacion(tipo = notification.tipo, conTitulo = notification.itemTitle.isNotEmpty()),
        notification.actorName,
        notification.itemTitle,
    )
    val evento = eventoDeNotificacion(tipo = notification.tipo)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .texturaDeTarjeta(RoundedCornerShape(18.dp))
            // Sin leer, encima de la textura va un tinte rosado; leida queda solo la textura
            .background(
                if (sinLeer) MaterialTheme.colorScheme.primary.copy(alpha = 0.14f) else Color.Transparent,
                RoundedCornerShape(18.dp),
            )
            .clickable { onClick() }
            .testTag("notificacion_${notification.id}")
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileAsyncImage(
            profileImage = notification.actorImage,
            contentDescription = stringResource(R.string.foto_perfil),
            size = 42
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Que paso, de un vistazo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = evento.icono,
                    contentDescription = null,
                    tint = if (sinLeer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(evento.titulo),
                    color = if (sinLeer) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Text(
                text = buildAnnotatedString {
                    // El nombre de quien la genero va en negrita y lo resenado en rosado. Casi todos
                    // los textos empiezan con el nombre; el del like a un comentario empieza con "A"
                    append(texto.substringBefore(notification.actorName))
                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(notification.actorName)
                    }
                    val resto = texto.substringAfter(notification.actorName)
                    if (notification.itemTitle.isNotEmpty() && resto.contains(notification.itemTitle)) {
                        append(resto.substringBefore(notification.itemTitle))
                        withStyle(
                            SpanStyle(fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
                        ) {
                            append(notification.itemTitle)
                        }
                        append(resto.substringAfter(notification.itemTitle))
                    } else {
                        append(resto)
                    }
                },
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (sinLeer) 1f else 0.8f),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (sinLeer) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.padding(top = 2.dp),
            )

            TiempoTranscurrido(timeAgo = notification.timeAgo, modifier = Modifier.padding(top = 2.dp))
        }

        // Un seguidor nuevo no trae miniatura
        if (notification.itemImage.isNotEmpty()) {
            Spacer(modifier = Modifier.width(10.dp))

            ItemAsyncImage(
                imagenUrl = notification.itemImage,
                titulo = notification.itemTitle,
                esquinas = 10.dp,
                modifier = Modifier.size(46.dp)
            )
        }

        if (sinLeer) {
            Spacer(modifier = Modifier.width(10.dp))

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .testTag("sinLeer_${notification.id}")
            )
        }
    }
}

// Que paso, con su icono: la primera linea de cada notificacion
private data class EventoDeNotificacion(
    @StringRes val titulo: Int,
    val icono: ImageVector,
)

private fun eventoDeNotificacion(tipo: String): EventoDeNotificacion = when (tipo) {
    NOTIFICACION_LIKE -> EventoDeNotificacion(R.string.evento_like, Icons.Outlined.Favorite)
    NOTIFICACION_LIKE_COMENTARIO -> EventoDeNotificacion(R.string.evento_like_comentario, Icons.Outlined.Favorite)
    NOTIFICACION_COMENTARIO -> EventoDeNotificacion(R.string.evento_comentario, Icons.AutoMirrored.Outlined.Comment)
    NOTIFICACION_RESPUESTA -> EventoDeNotificacion(R.string.evento_respuesta, Icons.Outlined.Reply)
    NOTIFICACION_REACCION_SONANDO -> EventoDeNotificacion(R.string.evento_reaccion, Icons.Outlined.LocalFireDepartment)
    // NOTIFICACION_SEGUIDOR
    else -> EventoDeNotificacion(R.string.evento_seguidor, Icons.Outlined.PersonAdd)
}

// El texto de cada tipo: siempre lleva el nombre de quien la hizo y, si tiene, el titulo del item.
// Sin titulo se usa la variante sin "de ..." o "en ..."
@StringRes
private fun textoDeNotificacion(tipo: String, conTitulo: Boolean): Int = when (tipo) {
    NOTIFICACION_LIKE -> if (conTitulo) R.string.notificacion_like else R.string.notificacion_like_sin_item
    NOTIFICACION_LIKE_COMENTARIO -> if (conTitulo) {
        R.string.notificacion_like_comentario
    } else {
        R.string.notificacion_like_comentario_sin_item
    }
    NOTIFICACION_COMENTARIO -> if (conTitulo) R.string.notificacion_comentario else R.string.notificacion_comentario_sin_item
    NOTIFICACION_RESPUESTA -> R.string.notificacion_respuesta
    NOTIFICACION_REACCION_SONANDO -> if (conTitulo) {
        R.string.notificacion_reaccion_sonando
    } else {
        R.string.notificacion_reaccion_sonando_sin_item
    }
    // NOTIFICACION_SEGUIDOR
    else -> R.string.notificacion_seguidor
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
fun NotificationRowLikeComentarioPreview() {
    MixtappTheme(darkTheme = true) {
        NotificationRow(
            notification = LocalPreviewProvider.notifications.first().copy(tipo = NOTIFICACION_LIKE_COMENTARIO),
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
