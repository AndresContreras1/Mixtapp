package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.ui.theme.MixtappTheme

// La tarjeta translucida de la app: una superficie mas clara que el fondo, sin borde, para que se
// separe del fondo por la luz y no por una linea. Los bordes quedan para los campos y para lo
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
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.6f)
    )

    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = forma, colors = colores, content = contenido)
    } else {
        Card(modifier = modifier, shape = forma, colors = colores, content = contenido)
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
