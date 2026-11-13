package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.OpcionOvalada

// El acceso a los datos curiosos del item: la opcion ovalada con el bombillo que abre su pantalla
@Composable
fun DatosCuriososButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OpcionOvalada(
        icono = Icons.Outlined.Lightbulb,
        texto = stringResource(R.string.datos_curiosos),
        onClick = onClick,
        modifier = modifier.testTag("btnDatosCuriosos"),
    )
}

// Pone el item en "sonando ahora": lo ven mis seguidores en Siguiendo y le pueden dar 🔥
@Composable
fun SonandoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OpcionOvalada(
        icono = Icons.Outlined.Headphones,
        texto = stringResource(R.string.estoy_escuchando_esto),
        onClick = onClick,
        modifier = modifier.testTag("btnSonando"),
    )
}

@Composable
@Preview(showBackground = true)
fun DatosCuriososButtonPreview() {
    MixtappTheme(darkTheme = true) {
        DatosCuriososButton(onClick = {})
    }
}

@Composable
@Preview(showBackground = true)
fun SonandoButtonPreview() {
    MixtappTheme(darkTheme = true) {
        SonandoButton(onClick = {})
    }
}
