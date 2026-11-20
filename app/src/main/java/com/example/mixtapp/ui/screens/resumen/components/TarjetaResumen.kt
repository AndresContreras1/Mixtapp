package com.example.mixtapp.ui.screens.resumen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Album
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.Black
import com.example.mixtapp.ui.theme.Coral
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.White
import com.example.mixtapp.ui.utils.PortadaConColor

// Los datos cortos (un numero, "4.5") van enormes; un nombre largo, grande pero sin cortarse
private const val LARGO_DE_UN_NUMERO = 5

// Una tarjeta de Tu año como historia: ocupa la pantalla con un degradado vivo, un icono musical, que
// se cuenta, la imagen grande (la portada del genero, la foto del artista o lo mejor calificado), el
// dato con su titulo abajo y una linea que lo explica. Con imagen, el degradado sale del color de esa
// imagen (Palette); sin imagen, de los colores que pasa la pantalla
@Composable
fun TarjetaResumen(
    titulo: String,
    valor: String,
    detalle: String,
    icono: ImageVector,
    colores: List<Color>,
    modifier: Modifier = Modifier,
    imagenUrl: String? = null,
) {
    // El color de la imagen solo decora: es de la vista, no del estado
    var colorDeLaImagen by remember(imagenUrl) { mutableStateOf<Color?>(null) }
    val color = colorDeLaImagen
    val degradado = if (color != null) listOf(color, lerp(color, Black, 0.65f)) else colores

    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(32.dp))
            .background(Brush.linearGradient(degradado))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = White.copy(alpha = 0.9f),
            modifier = Modifier.size(36.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = titulo,
            color = White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        if (imagenUrl != null) {
            Spacer(modifier = Modifier.height(20.dp))
            PortadaConColor(
                imagenUrl = imagenUrl,
                titulo = valor,
                esquinas = 28.dp,
                onColor = { colorDeLaImagen = it },
                modifier = Modifier
                    .size(230.dp)
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp)),
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = valor,
            color = White,
            style = if (valor.length <= LARGO_DE_UN_NUMERO) {
                MaterialTheme.typography.displayLarge
            } else {
                MaterialTheme.typography.displaySmall
            },
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = detalle,
            color = White.copy(alpha = 0.92f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview(showBackground = true, heightDp = 640)
fun TarjetaResumenPreview() {
    MixtappTheme(darkTheme = true) {
        TarjetaResumen(
            titulo = "El género que más te gusta",
            valor = "Nu metal",
            detalle = "Sale de tus me gusta y de lo que mejor calificaste",
            icono = Icons.Outlined.Album,
            colores = listOf(Coral, Magenta),
            imagenUrl = LocalPreviewProvider.fromZero.imagenUrl,
        )
    }
}
