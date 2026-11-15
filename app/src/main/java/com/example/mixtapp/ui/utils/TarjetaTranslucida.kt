package com.example.mixtapp.ui.utils

import androidx.compose.foundation.BorderStroke
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

// La tarjeta translucida de la app: deja ver el fondo con los circulos detras y la resalta un borde
// claro. La usan la resena, los datos y los datos curiosos del detalle, lo que se califica, los
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
        containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.8f)
    )
    val borde = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = forma, colors = colores, border = borde, content = contenido)
    } else {
        Card(modifier = modifier, shape = forma, colors = colores, border = borde, content = contenido)
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
