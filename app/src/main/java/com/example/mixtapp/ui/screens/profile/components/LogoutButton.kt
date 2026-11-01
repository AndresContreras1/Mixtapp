package com.example.mixtapp.ui.screens.profile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme

// Cerrar sesion: ovalado y con el borde del color de error, para que no se confunda con el boton
// principal. Esta al final del perfil y tambien en el error, para que nadie quede atrapado
@Composable
fun LogoutButton(
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onLogoutClick,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("btnLogout"),
        shape = CircleShape,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.6f))
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.Logout,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = stringResource(R.string.cerrar_sesion),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LogoutButtonPreview() {
    MixtappTheme(darkTheme = true) {
        LogoutButton(onLogoutClick = {})
    }
}
