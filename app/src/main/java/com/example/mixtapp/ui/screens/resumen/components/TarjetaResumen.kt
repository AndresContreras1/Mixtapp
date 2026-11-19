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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.Coral
import com.example.mixtapp.ui.theme.Magenta
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.theme.White
import com.example.mixtapp.ui.utils.ItemAsyncImage

// Los datos cortos (un numero, "4.5") van enormes; un nombre largo, grande pero sin cortarse
private const val LARGO_DE_UN_NUMERO = 5

// Una tarjeta de Tu año como historia: ocupa la pantalla con su propio degradado vivo, un icono
// musical, que se cuenta, el dato muy grande y una linea que lo explica. Si el dato es un item, va su
// portada. Los colores vienen de la pantalla para que cada tarjeta se vea distinta
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(32.dp))
            .background(Brush.linearGradient(colores))
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            tint = White.copy(alpha = 0.9f),
            modifier = Modifier.size(44.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = titulo,
            color = White.copy(alpha = 0.9f),
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        if (imagenUrl != null) {
            Spacer(modifier = Modifier.height(20.dp))
            ItemAsyncImage(
                imagenUrl = imagenUrl,
                titulo = valor,
                esquinas = 24.dp,
                modifier = Modifier.size(190.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = valor,
            color = White,
            style = if (valor.length <= LARGO_DE_UN_NUMERO) {
                MaterialTheme.typography.displayLarge
            } else {
                MaterialTheme.typography.displaySmall
            },
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = detalle,
            color = White.copy(alpha = 0.92f),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview(showBackground = true, heightDp = 560)
fun TarjetaResumenPreview() {
    MixtappTheme(darkTheme = true) {
        TarjetaResumen(
            titulo = "Lo que mejor calificaste",
            valor = LocalPreviewProvider.fromZero.titulo,
            detalle = "Linkin Park · 5/5",
            icono = Icons.Outlined.Album,
            colores = listOf(Coral, Magenta),
            imagenUrl = LocalPreviewProvider.fromZero.imagenUrl,
        )
    }
}
