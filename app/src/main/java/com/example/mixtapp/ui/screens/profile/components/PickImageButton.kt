package com.example.mixtapp.ui.screens.profile.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppButton

// Abre la galeria del celular y entrega la imagen elegida; si se cancela no hace nada
@Composable
fun PickImageButton(
    action: (Uri) -> Unit,
    cargando: Boolean,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { action(it) }
    }

    AppButton(
        texto = stringResource(R.string.cambiar_foto),
        onClick = { launcher.launch("image/*") },
        cargando = cargando,
        modifier = modifier.height(44.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun PickImageButtonPreview() {
    MixtappTheme(darkTheme = true) {
        PickImageButton(
            action = {},
            cargando = false
        )
    }
}
