package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.ui.theme.MixtappTheme

// El play redondo del fragmento de 30 segundos, en el rosado de lo importante. Sin estado muestra el
// play; mientras carga, el circulo; mientras suena, el cuadrado para detenerlo. El estado llega del
// ViewModel y el toque solo avisa
@Composable
fun BotonDeVistaPrevia(
    // null si este item no esta sonando
    estado: EstadoDeReproduccion?,
    titulo: String,
    tamano: Dp,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = modifier.size(tamano),
        shape = CircleShape,
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            when (estado) {
                EstadoDeReproduccion.Cargando -> CircularProgressIndicator(
                    modifier = Modifier.size(tamano * 0.45f),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp,
                )
                EstadoDeReproduccion.Sonando -> Icon(
                    imageVector = Icons.Filled.Stop,
                    contentDescription = stringResource(R.string.detener_fragmento),
                    modifier = Modifier.size(tamano * 0.5f),
                )
                else -> Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription = stringResource(R.string.escuchar_fragmento_de, titulo),
                    modifier = Modifier.size(tamano * 0.55f),
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun BotonDeVistaPreviaPreview() {
    MixtappTheme(darkTheme = true) {
        BotonDeVistaPrevia(estado = null, titulo = "Meteora", tamano = 48.dp, onClick = {})
    }
}

@Composable
@Preview(showBackground = true)
fun BotonDeVistaPreviaSonandoPreview() {
    MixtappTheme(darkTheme = true) {
        BotonDeVistaPrevia(estado = EstadoDeReproduccion.Sonando, titulo = "Meteora", tamano = 48.dp, onClick = {})
    }
}
