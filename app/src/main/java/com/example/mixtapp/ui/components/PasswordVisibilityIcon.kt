package com.example.mixtapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun PasswordVisibilityIcon(
    contrasenaVisible: Boolean,
    modifier: Modifier = Modifier
) {
    Icon(
        painter = painterResource(
            id = if (contrasenaVisible) {
                R.drawable.visible
            } else {
                R.drawable.invisible
            }
        ),
        contentDescription = stringResource(
            if (contrasenaVisible) R.string.ocultar_contrasena else R.string.mostrar_contrasena
        ),
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.size(18.dp)
    )
}


@Composable
@Preview
fun PasswordVisibilityIconPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PasswordVisibilityIcon(contrasenaVisible = false)
            PasswordVisibilityIcon(contrasenaVisible = true)
        }
    }
}
