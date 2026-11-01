package com.example.mixtapp.ui.screens.search.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// El titulo de la categoria elegida con la X que la quita y vuelve a mostrar las tarjetas
@Composable
fun SelectedCategoryHeader(
    titulo: String,
    onClearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = titulo,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )

        IconButton(onClick = onClearClick) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(R.string.quitar_categoria),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun SelectedCategoryHeaderPreview() {
    MixtappTheme(darkTheme = true) {
        SelectedCategoryHeader(
            titulo = stringResource(R.string.categoria_mas_populares),
            onClearClick = {},
        )
    }
}
