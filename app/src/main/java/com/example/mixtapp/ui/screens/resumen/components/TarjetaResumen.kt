package com.example.mixtapp.ui.screens.resumen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ItemAsyncImage

// Una tarjeta del resumen: que se cuenta, el dato grande y una linea que lo explica. Si el dato es
// un item, va su portada
@Composable
fun TarjetaResumen(
    titulo: String,
    valor: String,
    detalle: String,
    modifier: Modifier = Modifier,
    imagenUrl: String? = null,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.85f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )

            if (imagenUrl != null) {
                Spacer(modifier = Modifier.height(16.dp))
                ItemAsyncImage(
                    imagenUrl = imagenUrl,
                    titulo = valor,
                    esquinas = 20.dp,
                    modifier = Modifier.size(160.dp),
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = valor,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = detalle,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun TarjetaResumenPreview() {
    MixtappTheme(darkTheme = true) {
        TarjetaResumen(
            titulo = "Lo que mejor calificaste",
            valor = LocalPreviewProvider.fromZero.titulo,
            detalle = "5 estrellas",
            imagenUrl = LocalPreviewProvider.fromZero.imagenUrl,
        )
    }
}
