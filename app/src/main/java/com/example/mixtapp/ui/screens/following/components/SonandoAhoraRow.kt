package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.SonandoUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.ProfileAsyncImage

// La fila "Sonando ahora" de Siguiendo: lo que escucho yo y lo que escuchan quienes sigo
@Composable
fun SonandoAhoraRow(
    sonando: List<SonandoUi>,
    onItemClick: (SonandoUi) -> Unit,
    onUserClick: (String) -> Unit,
    onReaccionarClick: (SonandoUi) -> Unit,
    onQuitarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(sonando, key = { it.usuarioId }) { item ->
            SonandoCard(
                sonando = item,
                onItemClick = { onItemClick(item) },
                onUserClick = { onUserClick(item.usuarioId) },
                onReaccionarClick = { onReaccionarClick(item) },
                onQuitarClick = onQuitarClick,
            )
        }
    }
}

// Una tarjeta: la portada (abre el item), quien lo escucha y hace cuanto (abre su perfil) y el 🔥
// con su contador. En la mia, en vez del 🔥 esta el boton para quitarlo
@Composable
fun SonandoCard(
    sonando: SonandoUi,
    onItemClick: () -> Unit,
    onUserClick: () -> Unit,
    onReaccionarClick: () -> Unit,
    onQuitarClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .width(160.dp)
            .testTag("sonando_${sonando.usuarioId}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
        ),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            ItemAsyncImage(
                imagenUrl = sonando.item.imagenUrl,
                titulo = sonando.item.titulo,
                esquinas = 14.dp,
                modifier = Modifier
                    .size(140.dp)
                    .clickable { onItemClick() },
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = sonando.item.titulo,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = sonando.item.subtitulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.clickable { onUserClick() },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileAsyncImage(
                    profileImage = sonando.fotoUrl,
                    contentDescription = stringResource(R.string.foto_perfil),
                    size = 24,
                )
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (sonando.esMio) stringResource(R.string.tu) else sonando.nombre,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        // Hace menos de un minuto el mapeo deja el texto vacio
                        text = sonando.timeAgo.ifEmpty { stringResource(R.string.ahora) },
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelSmall,
                    )
                }
            }

            if (sonando.esMio) {
                TextButton(
                    onClick = onQuitarClick,
                    modifier = Modifier.testTag("btnQuitarSonando"),
                ) {
                    Text(text = stringResource(R.string.dejar_de_sonar))
                }
            } else {
                TextButton(
                    onClick = onReaccionarClick,
                    modifier = Modifier.testTag("btnReaccion_${sonando.usuarioId}"),
                ) {
                    Text(
                        text = stringResource(R.string.reaccion_fuego, sonando.reacciones),
                        color = if (sonando.reaccionado) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (sonando.reaccionado) FontWeight.Bold else FontWeight.Normal,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SonandoAhoraRowPreview() {
    MixtappTheme(darkTheme = true) {
        SonandoAhoraRow(
            sonando = listOf(
                SonandoUi(
                    usuarioId = "yo",
                    nombre = "Andres",
                    fotoUrl = "",
                    item = LocalPreviewProvider.fromZero.toItemResumenUi(),
                    timeAgo = "5 min",
                    reacciones = 0,
                    reaccionado = false,
                    esMio = true,
                ),
                SonandoUi(
                    usuarioId = "laura",
                    nombre = "Laura Gómez",
                    fotoUrl = "",
                    item = LocalPreviewProvider.karolG.toItemResumenUi(),
                    timeAgo = "1 h",
                    reacciones = 3,
                    reaccionado = true,
                    esMio = false,
                ),
            ),
            onItemClick = {},
            onUserClick = {},
            onReaccionarClick = {},
            onQuitarClick = {},
        )
    }
}
