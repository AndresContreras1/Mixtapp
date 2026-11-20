package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

// La tarjeta de la app con textura (texturaDeTarjeta): sombra suave, degradado y grano, para que se
// separe del fondo y no se vea plana. Los bordes de color quedan para los campos y para lo
// seleccionado. La usan la resena, los datos y los datos curiosos del detalle, lo que se califica, los
// resultados de la busqueda, los lanzamientos, las mixtapes y lo que suena. onClick es null si la
// tarjeta no se toca
@Composable
fun TarjetaTranslucida(
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    val forma = RoundedCornerShape(24.dp)
    val colores = CardDefaults.cardColors(
        containerColor = Color.Transparent
    )

    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier.rebote().texturaDeTarjeta(forma), shape = forma, colors = colores, content = contenido)
    } else {
        Card(modifier = modifier.texturaDeTarjeta(forma), shape = forma, colors = colores, content = contenido)
    }
}

@Composable
@Preview(showBackground = true)
fun TarjetaTranslucidaPreview() {
    MixtappTheme(darkTheme = true) {
        TarjetaTranslucida(onClick = null) {
            Text(text = "Meteora", modifier = Modifier.padding(16.dp))
        }
    }
}
