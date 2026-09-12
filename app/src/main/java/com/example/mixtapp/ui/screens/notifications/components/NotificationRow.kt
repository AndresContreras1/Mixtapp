package com.example.mixtapp.ui.screens.notifications.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.components.AlbumAsyncImage
import com.example.mixtapp.ui.screens.notifications.model.NotificationUi

@Composable
fun NotificationRow(notification: NotificationUi, modifier: Modifier = Modifier) {
    // Las notificaciones ya leidas se ven un poco atenuadas
    val alpha = if (notification.isRead) 0.55f else 1f

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f * alpha)),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = notification.actorInitials,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = buildNotificationMessage(notification),
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha),
                fontSize = 14.sp,
                lineHeight = 19.sp,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.timeAgo,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f * alpha),
                fontSize = 12.sp,
            )
        }

        notification.thumbnail?.let { url ->
            Spacer(modifier = Modifier.width(10.dp))
            AlbumAsyncImage(
                cover = url,
                contentDescription = stringResource(R.string.notification_thumbnail),
                alpha = 1f,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp)),
            )
        }
    }
}

// El nombre del actor y la palabra resaltada van en negrita; el resto en texto normal
@Composable
private fun buildNotificationMessage(notification: NotificationUi) = buildAnnotatedString {
    withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
        append(notification.actorName)
    }
    append(" ")

    val word = notification.highlightedWord
    if (word != null && notification.message.contains(word)) {
        val index = notification.message.indexOf(word)
        append(notification.message.substring(0, index))
        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)) {
            append(word)
        }
        append(notification.message.substring(index + word.length))
    } else {
        append(notification.message)
    }
}