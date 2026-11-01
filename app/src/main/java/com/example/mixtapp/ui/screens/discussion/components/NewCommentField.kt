package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppTextField

// El campo para comentar, fijo abajo de la discusion con el boton de enviar al final. Si se esta
// respondiendo, arriba dice a quien y la X lo cancela
@Composable
fun NewCommentField(
    value: String,
    // El nombre de quien escribio el comentario al que se responde; null si se comenta la resena
    respondiendoA: String?,
    publicando: Boolean,
    onValueChange: (String) -> Unit,
    onCancelReplyClick: () -> Unit,
    onPublicarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = respondiendoA != null) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    // Mientras se desvanece ya es null: se queda vacio en vez de mostrar otro nombre
                    text = stringResource(R.string.respondiendo_a, respondiendoA ?: ""),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )

                IconButton(onClick = onCancelReplyClick) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(R.string.cancelar_respuesta),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp),
                    )
                }
            }
        }

        AppTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = stringResource(R.string.comentario_placeholder),
            modifier = Modifier.testTag("txtComentario"),
            trailingContent = {
                // Mientras se guarda no se puede mandar otro
                IconButton(
                    onClick = onPublicarClick,
                    enabled = !publicando,
                    modifier = Modifier.testTag("btnPublicarComentario"),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = stringResource(R.string.publicar_comentario),
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )
    }
}

@Composable
@Preview(showBackground = true)
fun NewCommentFieldPreview() {
    MixtappTheme(darkTheme = true) {
        NewCommentField(
            value = "",
            respondiendoA = "Valentina Torres",
            publicando = false,
            onValueChange = {},
            onCancelReplyClick = {},
            onPublicarClick = {},
        )
    }
}
