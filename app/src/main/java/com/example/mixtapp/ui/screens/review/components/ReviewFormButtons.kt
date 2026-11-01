package com.example.mixtapp.ui.screens.review.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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

// Cancelar y Publicar simetricos: los dos ovalados, del mismo alto que el boton de la app y con el
// mismo ancho. Mientras se guarda, Publicar muestra que esta cargando
@Composable
fun ReviewFormButtons(
    publicando: Boolean,
    onCancel: () -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier
                .weight(1f)
                .height(56.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            Text(
                text = stringResource(R.string.cancel),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        AppButton(
            texto = stringResource(R.string.post),
            onClick = onPostClick,
            cargando = publicando,
            modifier = Modifier
                .weight(1f)
                .testTag("btnPublicar"),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ReviewFormButtonsPreview() {
    MixtappTheme(darkTheme = true) {
        ReviewFormButtons(
            publicando = false,
            onCancel = {},
            onPostClick = {},
        )
    }
}
