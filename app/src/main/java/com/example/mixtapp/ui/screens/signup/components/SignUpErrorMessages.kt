package com.example.mixtapp.ui.screens.signup.components

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
    mostrarErrorContrasenas: Boolean,
    errorMessageRes: Int?,
    modifier: Modifier = Modifier
) {
    // Centrado igual que en la pantalla, para que los mensajes no se corran al extraerlos
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Aviso mientras se escribe: las dos contrasenas no coinciden
        if (mostrarErrorContrasenas) {
            Text(
                text = stringResource(R.string.passwords_no_coinciden),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }

        // Error del intento de crear la cuenta, calculado por el ViewModel
        if (errorMessageRes != null) {
            Text(
                text = stringResource(errorMessageRes),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
        }
    }
}
