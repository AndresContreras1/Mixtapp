package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.DiscussionCommentUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.LikeButton
import com.example.mixtapp.ui.utils.ProfileAsyncImage

// Un comentario de la discusion: la foto, el nombre, cuanto hace, el texto, el like y Responder.
// Las respuestas van con sangria a la izquierda para que se vea el hilo
@Composable
fun ThreadCommentItem(
    comment: DiscussionCommentUi,
    // Si es el comentario al que se esta respondiendo, Responder se resalta
    isReplying: Boolean,
    onAuthorClick: (String) -> Unit,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = if (comment.isReply) 44.dp else 0.dp, top = 8.dp),
    ) {
        // La foto y el nombre abren el perfil de quien lo escribio
        ProfileAsyncImage(
            profileImage = comment.authorImage,
            contentDescription = stringResource(R.string.foto_perfil),
            size = if (comment.isReply) 30 else 36,
            modifier = Modifier
                .clip(CircleShape)
                .clickable { onAuthorClick(comment.authorId) },
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.author,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable { onAuthorClick(comment.authorId) },
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    // Hace menos de un minuto el mapeo deja el texto vacio
                    text = comment.timeAgo.ifEmpty { stringResource(R.string.ahora) },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            Text(
                text = comment.content,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 2.dp),
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                LikeButton(
                    likesText = comment.likesText,
                    isLiked = comment.isLiked,
                    onLikeClick = onLikeClick,
                    contentDescriptionRes = R.string.like_comment,
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextButton(onClick = onReplyClick) {
                    Text(
                        text = stringResource(if (isReplying) R.string.replying else R.string.reply),
                        color = if (isReplying) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ThreadCommentItemPreview() {
    MixtappTheme(darkTheme = true) {
        Column {
            LocalPreviewProvider.comentarios.forEach { comentario ->
                ThreadCommentItem(
                    comment = comentario,
                    isReplying = false,
                    onAuthorClick = {},
                    onLikeClick = {},
                    onReplyClick = {},
                )
            }
        }
    }
}
