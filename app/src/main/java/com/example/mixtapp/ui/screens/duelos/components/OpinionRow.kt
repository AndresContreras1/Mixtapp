package com.example.mixtapp.ui.screens.duelos.components

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
import com.example.mixtapp.data.model.OPCION_A
import com.example.mixtapp.data.model.VotoUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ProfileAsyncImage

// La opinion de quien voto: su foto y su nombre (tocarlos abre su perfil), por cual voto, cuanto
// hace y lo que escribio. El color de "voto por" es el del lado de la barra
@Composable
fun OpinionRow(
    voto: VotoUi,
    tituloVotado: String,
    onAuthorClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f))
            .testTag("opinion_${voto.usuarioId}")
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        ProfileAsyncImage(
            profileImage = voto.autorFoto,
            contentDescription = stringResource(R.string.foto_perfil),
            size = 40,
            modifier = Modifier.clickable { onAuthorClick() }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (voto.esMio) stringResource(R.string.tu) else voto.autor,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .clickable { onAuthorClick() },
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    // Hace menos de un minuto el mapeo deja el texto vacio
                    text = voto.timeAgo.ifEmpty { stringResource(R.string.ahora) },
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.labelSmall,
                )
            }

            Text(
                text = stringResource(R.string.voto_por, tituloVotado),
                color = if (voto.opcion == OPCION_A) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
            )

            if (voto.opinion.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = voto.opinion,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun OpinionRowPreview() {
    MixtappTheme(darkTheme = true) {
        OpinionRow(
            voto = VotoUi(
                usuarioId = "usuario_1",
                autor = "Laura Gómez",
                autorFoto = "",
                opcion = OPCION_A,
                opinion = "El riff de la primera canción no tiene comparación.",
                timeAgo = "3 h",
                esMio = false,
            ),
            tituloVotado = "Master of Puppets",
            onAuthorClick = {},
        )
    }
}
