package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.ui.theme.MixtappTheme

// El encabezado de un item: la flecha, la imagen grande y debajo tres niveles de texto que se
// distinguen por tamano y peso: el titulo grande en negrita, el subtitulo (el album o el artista)
// mediano y el artista de la cancion mas pequeno y tenue. El ano y los generos van juntos en una
// sola linea de metadatos. Lo usan el detalle y los datos curiosos, asi las dos pantallas se ven
// iguales arriba
@Composable
fun ItemHeader(
    item: ItemUi,
    onBackClick: () -> Unit,
    // Avisa el color de la portada para el degradado del detalle; null si la pantalla no lo usa
    onColorDePortada: ((Color) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    // Los artistas no tienen ano, y si el subgenero es el mismo genero no se repite
    val datos = listOf(item.anio, item.genero, item.subgenero)
        .filter { it.isNotEmpty() }
        .distinct()

    Column(modifier = modifier.fillMaxWidth()) {
        BackButton(onBackClick = onBackClick)

        Spacer(modifier = Modifier.height(16.dp))

        val modificadorPortada = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
        if (onColorDePortada != null) {
            PortadaConColor(
                imagenUrl = item.imagenUrl,
                titulo = item.titulo,
                esquinas = 28.dp,
                onColor = onColorDePortada,
                modifier = modificadorPortada,
            )
        } else {
            ItemAsyncImage(
                imagenUrl = item.imagenUrl,
                titulo = item.titulo,
                esquinas = 28.dp,
                modifier = modificadorPortada,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = item.titulo,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.testTag("tituloItem"),
        )

        Text(
            text = item.subtitulo,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(top = 2.dp)
                .testTag("subtituloItem"),
        )

        // Solo las canciones traen el artista aparte del album
        if (item.detalle.isNotEmpty()) {
            Text(
                text = item.detalle,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleSmall,
            )
        }

        // El ano y los generos solo describen el item: una linea tenue y no chips que parezcan filtros
        if (datos.isNotEmpty()) {
            Text(
                text = datos.joinToString(separator = "  ·  "),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = 10.dp),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ItemHeaderPreview() {
    MixtappTheme(darkTheme = true) {
        ItemHeader(
            item = LocalPreviewProvider.emptinessMachine,
            onBackClick = {},
            onColorDePortada = null,
        )
    }
}
