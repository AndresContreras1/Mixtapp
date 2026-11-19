package com.example.mixtapp.ui.screens.userprofile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppButton

// Seguir con el boton principal rosado. Si ya lo sigo, el boton cambia claramente: "Siguiendo" con el
// check, borde rosado y fondo tenue; tocarlo deja de seguir. Los dos tienen el mismo testTag porque
// son el mismo boton para las pruebas
@Composable
fun FollowButton(
    siguiendo: Boolean,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (siguiendo) {
        OutlinedButton(
            onClick = onFollowClick,
            modifier = modifier
                .padding(top = 14.dp)
                .width(180.dp)
                .height(44.dp)
                .testTag("btnSeguir"),
            shape = CircleShape,
            border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            ),
        ) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(R.string.siguiendo_estado),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall,
            )
        }
    } else {
        AppButton(
            texto = stringResource(R.string.seguir_usuario),
            onClick = onFollowClick,
            modifier = modifier
                .padding(top = 14.dp)
                .width(180.dp)
                .height(44.dp)
                .testTag("btnSeguir"),
            cargando = false,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun FollowButtonPreview() {
    MixtappTheme(darkTheme = true) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FollowButton(siguiendo = false, onFollowClick = {})
            FollowButton(siguiendo = true, onFollowClick = {})
        }
    }
}
