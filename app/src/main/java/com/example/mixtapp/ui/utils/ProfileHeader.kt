package com.example.mixtapp.ui.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.MixtappTheme

// El encabezado de un perfil: la foto, el nombre, el @usuario, la biografia y los contadores. Lo
// usan mi perfil y el de otro usuario; cada uno pone debajo su accion (cambiar la foto o seguir)
@Composable
fun ProfileHeader(
    nombre: String,
    username: String,
    bio: String,
    fotoUrl: String,
    seguidores: Int,
    seguidos: Int,
    onSeguidoresClick: () -> Unit,
    onSeguidosClick: () -> Unit,
    modifier: Modifier = Modifier,
    accion: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileAsyncImage(
            profileImage = fotoUrl,
            contentDescription = stringResource(R.string.foto_perfil),
            modifier = Modifier
                .size(104.dp)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sin documento todavia no hay nombre: solo sale el usuario del correo
        if (nombre.isNotEmpty()) {
            Text(
                text = nombre,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.testTag("nombrePerfil")
            )
        }

        Text(
            text = stringResource(R.string.arroba_usuario, username),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )

        if (bio.isNotEmpty()) {
            Text(
                text = bio,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tocar un contador abre esa lista
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ProfileCounter(
                texto = stringResource(R.string.cantidad_seguidores, seguidores),
                onClick = onSeguidoresClick,
                modifier = Modifier.testTag("contadorSeguidores")
            )
            ProfileCounter(
                texto = stringResource(R.string.cantidad_seguidos, seguidos),
                onClick = onSeguidosClick,
                modifier = Modifier.testTag("contadorSeguidos")
            )
        }

        accion()
    }
}

// Un contador del encabezado, ovalado como los filtros, para que se note que se puede tocar
@Composable
fun ProfileCounter(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = texto,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}

@Composable
@Preview(showBackground = true)
fun ProfileHeaderPreview() {
    MixtappTheme(darkTheme = true) {
        val usuario = LocalPreviewProvider.usuario

        ProfileHeader(
            nombre = usuario.nombre,
            username = usuario.username,
            bio = usuario.bio,
            fotoUrl = usuario.fotoUrl,
            seguidores = usuario.seguidores,
            seguidos = usuario.seguidos,
            onSeguidoresClick = {},
            onSeguidosClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileCounterPreview() {
    MixtappTheme(darkTheme = true) {
        ProfileCounter(
            texto = stringResource(R.string.cantidad_seguidores, LocalPreviewProvider.usuario.seguidores),
            onClick = {}
        )
    }
}
