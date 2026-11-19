package com.example.mixtapp.ui.screens.profile.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ProfileAsyncImage

// Mi foto de perfil, que se cambia al tocarla: la camara rosada en la esquina lo indica. Abre la
// galeria del celular y entrega la imagen elegida; si se cancela no hace nada. Mientras la foto sube
// se ve el circulo encima y no se puede tocar otra vez. Asi Cambiar foto ya no ocupa lugar entre las
// pestanas
@Composable
fun PickImageButton(
    fotoUrl: String,
    action: (Uri) -> Unit,
    cargando: Boolean,
    modifier: Modifier = Modifier
) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { action(it) }
    }

    Box(
        modifier = modifier
            .size(88.dp)
            .clickable(enabled = !cargando) { launcher.launch("image/*") }
            .testTag("tab_cambiar_foto"),
    ) {
        ProfileAsyncImage(
            profileImage = fotoUrl,
            contentDescription = stringResource(R.string.foto_perfil),
            size = 84,
            modifier = Modifier
                .align(Alignment.Center)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        if (cargando) {
            CircularProgressIndicator(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(32.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 3.dp
            )
        }

        Icon(
            imageVector = Icons.Filled.PhotoCamera,
            contentDescription = stringResource(R.string.cambiar_foto),
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .size(30.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .padding(6.dp),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun PickImageButtonPreview() {
    MixtappTheme(darkTheme = true) {
        PickImageButton(fotoUrl = "", action = {}, cargando = false)
    }
}
