package com.example.mixtapp.ui.utils

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// El error debajo de un formulario: aparece y se desvanece cuando cambia el estado. El testTag es
// para que las pruebas e2e encuentren el mensaje
@Composable
fun ErrorMessage(
    @StringRes messageRes: Int?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = messageRes != null,
        modifier = modifier,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Text(
            // Mientras se desvanece ya es null: se queda vacio en vez de mostrar otro texto
            text = if (messageRes != null) stringResource(messageRes) else "",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.testTag("errorMessage"),
        )
    }
}

// El error que ocupa toda la pantalla cuando no hay nada que mostrar: el tercer caso del when de
// cada pantalla (cargando, error o contenido)
@Composable
fun ErrorBox(
    @StringRes messageRes: Int,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(
            text = stringResource(messageRes),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(horizontal = 32.dp)
                .testTag("errorMessage"),
        )
    }
}

// Lo que se muestra cuando una lista esta vacia
@Composable
fun EmptyMessage(
    @StringRes messageRes: Int,
    modifier: Modifier = Modifier
) {
    Text(
        text = stringResource(messageRes),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        style = MaterialTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        modifier = modifier.padding(vertical = 24.dp, horizontal = 16.dp),
    )
}

@Composable
@Preview(showBackground = true)
fun ErrorMessagePreview() {
    MixtappTheme(darkTheme = true) {
        ErrorMessage(messageRes = R.string.error_generico)
    }
}

@Composable
@Preview(showBackground = true)
fun ErrorBoxPreview() {
    MixtappTheme(darkTheme = true) {
        ErrorBox(messageRes = R.string.error_cargar_contenido)
    }
}

@Composable
@Preview(showBackground = true)
fun EmptyMessagePreview() {
    MixtappTheme(darkTheme = true) {
        EmptyMessage(messageRes = R.string.sin_mis_resenas)
    }
}
