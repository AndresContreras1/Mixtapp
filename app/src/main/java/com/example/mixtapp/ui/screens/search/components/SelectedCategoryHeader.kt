package com.example.mixtapp.ui.screens.search.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppChip
import com.example.mixtapp.ui.utils.BackButton

// El titulo de la categoria elegida con la flecha que vuelve a las tarjetas (una X se confundia con
// cerrar o borrar la busqueda). En los lanzamientos, el titulo dice el orden y el chip lo invierte
@Composable
fun SelectedCategoryHeader(
    titulo: String,
    // El texto del chip del orden; null en las categorias que no se ordenan por fecha
    orden: String?,
    onBackClick: () -> Unit,
    onOrdenClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            BackButton(onBackClick = onBackClick)

            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .weight(1f),
            )
        }

        if (orden != null && onOrdenClick != null) {
            AppChip(
                text = orden,
                isSelected = true,
                onClick = onOrdenClick,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .testTag("chipOrden"),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SelectedCategoryHeaderPreview() {
    MixtappTheme(darkTheme = true) {
        SelectedCategoryHeader(
            titulo = stringResource(R.string.lanzamientos_recientes),
            orden = stringResource(R.string.orden_mas_recientes),
            onBackClick = {},
            onOrdenClick = {},
        )
    }
}
