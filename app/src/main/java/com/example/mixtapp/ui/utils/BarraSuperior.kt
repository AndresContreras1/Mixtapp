package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// La barra de arriba de las pestanas: queda fija y la lista se desliza debajo, pero sin panel de
// color: se funde con el fondo y sus burbujas. Solo la barra de abajo es solida. Cada pantalla pone
// adentro su titulo y, si los tiene, sus accesos o sus pestanas
@Composable
fun BarraSuperior(
    modifier: Modifier = Modifier,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        content = contenido,
    )
}

@Composable
@Preview(showBackground = true)
fun BarraSuperiorPreview() {
    MixtappTheme(darkTheme = true) {
        BarraSuperior {
            ScreenTitle(title = stringResource(R.string.search_title), onBackClick = null)
        }
    }
}
