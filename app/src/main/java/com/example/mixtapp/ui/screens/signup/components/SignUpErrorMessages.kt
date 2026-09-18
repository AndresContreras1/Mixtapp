package com.example.mixtapp.ui.screens.signup.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R

@Composable
fun SignUpErrorMessages(
    errorMessageRes: Int?,
    modifier: Modifier = Modifier
) {
    // Centrado igual que en la pantalla, para que los mensajes no se corran al extraerlos
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Error del intento de crear la cuenta, calculado por el ViewModel
        AnimatedVisibility(
            visible = errorMessageRes != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = stringResource(errorMessageRes ?: R.string.error_registro),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}
