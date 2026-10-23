package com.example.mixtapp.ui.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.ui.theme.MixtappTheme

// La carga de una pantalla completa: el circulo en el centro mientras llegan los datos
@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
@Preview(showBackground = true)
fun LoadingBoxPreview() {
    MixtappTheme(darkTheme = true) {
        LoadingBox()
    }
}
