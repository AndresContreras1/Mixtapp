package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// La flecha para volver: la usan los encabezados de varias pantallas
@Composable
fun BackButton(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onBackClick,
        modifier = modifier.size(30.dp)
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(R.string.back),
            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.72f),
            modifier = Modifier.size(30.dp)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun BackButtonPreview() {
    MixtappTheme(darkTheme = true) {
        BackButton(onBackClick = {})
    }
}
