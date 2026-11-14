package com.example.mixtapp.ui.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// Hace cuanto paso algo (una resena, un comentario, un voto, una notificacion): el texto pequeno
// debajo del nombre. Hace menos de un minuto el mapeo deja el texto vacio y se ve "Ahora"
@Composable
fun TiempoTranscurrido(
    timeAgo: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = timeAgo.ifEmpty { stringResource(R.string.ahora) },
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.labelSmall,
        modifier = modifier,
    )
}

@Composable
@Preview(showBackground = true)
fun TiempoTranscurridoPreview() {
    MixtappTheme(darkTheme = true) {
        TiempoTranscurrido(timeAgo = "")
    }
}
