package com.example.mixtapp.ui.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.MixtappTheme

// El encabezado de un perfil: la foto pequena a la izquierda y a su derecha el nombre, el @usuario y
// los contadores; debajo, la biografia y el Instagram. Lo usan mi perfil y el de otro usuario; cada
// uno pone debajo su accion (los avisos de mi perfil o el boton de seguir)
@Composable
fun ProfileHeader(
    nombre: String,
    username: String,
    bio: String,
    fotoUrl: String,
    seguidores: Int,
    seguidos: Int,
    instagram: String,
    instagramUrl: String,
    onSeguidoresClick: () -> Unit,
    onSeguidosClick: () -> Unit,
    modifier: Modifier = Modifier,
    accion: @Composable () -> Unit = {},
) {
    // LocalUriHandler abre el link en el navegador o en la app de Instagram si la tiene
    val uriHandler = LocalUriHandler.current

    Column(modifier = modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAsyncImage(
                profileImage = fotoUrl,
                contentDescription = stringResource(R.string.foto_perfil),
                size = 80,
                modifier = Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                // Sin documento todavia no hay nombre: solo sale el usuario del correo
                if (nombre.isNotEmpty()) {
                    Text(
                        text = nombre,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("nombrePerfil")
                    )
                }

                Text(
                    text = stringResource(R.string.arroba_usuario, username),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )

                // Tocar un contador abre esa lista
                ProfileCounter(
                    texto = stringResource(R.string.cantidad_seguidores, seguidores),
                    onClick = onSeguidoresClick,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .testTag("contadorSeguidores")
                )
                ProfileCounter(
                    texto = stringResource(R.string.cantidad_seguidos, seguidos),
                    onClick = onSeguidosClick,
                    modifier = Modifier.testTag("contadorSeguidos")
                )
            }
        }

        if (bio.isNotEmpty()) {
            Text(
                text = bio,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        // Solo sale si puso su Instagram; la camara lo distingue del @usuario de Mixtapp
        if (instagramUrl.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { uriHandler.openUri(instagramUrl) }
                    .padding(vertical = 4.dp)
                    .testTag("linkInstagram"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = stringResource(R.string.arroba_usuario, instagram),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        accion()
    }
}

// Un contador del encabezado, en negrilla para que se note que se puede tocar
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
            .clickable { onClick() }
            .padding(vertical = 2.dp)
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
            instagram = usuario.instagram,
            instagramUrl = usuario.instagramUrl,
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
