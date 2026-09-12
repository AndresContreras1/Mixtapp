package com.example.mixtapp.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R

@Composable
fun PasswordVisibilityIcon(
    passwordVisible: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(
            id = if (passwordVisible) {
                R.drawable.visible
            } else {
                R.drawable.invisible
            }
        ),
        contentDescription = stringResource(
            if (passwordVisible) R.string.ocultar_contrasena else R.string.mostrar_contrasena
        ),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(18.dp)
    )
}
