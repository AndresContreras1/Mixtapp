package com.example.mixtapp.ui.screens.userprofile.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
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
import com.example.mixtapp.ui.utils.AppButton

// Seguir con el boton principal; si ya lo sigo, dejar de seguir va solo con el borde, para que se
// note la diferencia. Los dos tienen el mismo testTag porque son el mismo boton para las pruebas
@Composable
fun FollowButton(
    siguiendo: Boolean,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    if (siguiendo) {
        TextButton(
            onClick = onFollowClick,
            modifier = modifier
                .width(180.dp)
                .height(44.dp)
                .testTag("btnSeguir"),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text(
                text = stringResource(R.string.dejar_de_seguir),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    } else {
        AppButton(
            texto = stringResource(R.string.seguir_usuario),
            onClick = onFollowClick,
            modifier = modifier
                .width(180.dp)
                .height(44.dp)
                .testTag("btnSeguir")
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
