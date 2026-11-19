package com.example.mixtapp.ui.utils

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// Lo que dejan abajo las listas de las pantallas con el +: el alto del boton y su margen, asi el
// ultimo elemento nunca queda debajo del boton
val EspacioDelFab = 96.dp

// El + de las pantallas con barra: abre la lista para elegir que calificar
@Composable
fun WriteReviewFab(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        shape = CircleShape
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = stringResource(R.string.write_review)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun WriteReviewFabPreview() {
    MixtappTheme(darkTheme = true) {
        WriteReviewFab(onClick = {})
    }
}
