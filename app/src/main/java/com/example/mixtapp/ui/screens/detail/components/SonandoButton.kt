package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.OpcionOvalada

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
fun SonandoButtonPreview() {
    MixtappTheme(darkTheme = true) {
        SonandoButton(onClick = {})
    }
}
