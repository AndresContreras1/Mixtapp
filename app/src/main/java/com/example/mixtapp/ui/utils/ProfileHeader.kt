package com.example.mixtapp.ui.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.MixtappTheme

// La cabecera compacta de un perfil, en un solo bloque: la foto, el nombre en negrita y el @usuario
// tenue; debajo, los contadores en una fila con el numero grande; despues la biografia y el Instagram
// como enlace. Lo usan mi perfil (con la foto que se cambia al tocarla) y el de otro usuario; cada uno
// pone debajo su accion (los avisos de mi perfil o el boton de seguir)
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
    // La foto que se puede cambiar, en mi perfil; null en el de otro, que solo la muestra
    fotoEditable: (@Composable () -> Unit)?,
    accion: @Composable () -> Unit,
) {
    // LocalUriHandler abre el link en el navegador o en la app de Instagram si la tiene
    val uriHandler = LocalUriHandler.current

    TarjetaTranslucida(onClick = null, modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (fotoEditable != null) {
                    fotoEditable()
                } else {
                    ProfileAsyncImage(
                        profileImage = fotoUrl,
                        contentDescription = stringResource(R.string.foto_perfil),
                        size = 84,
                        modifier = Modifier.border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    // Sin documento todavia no hay nombre: solo sale el usuario del correo
                    if (nombre.isNotEmpty()) {
                        Text(
                            text = nombre,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.headlineSmall,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.testTag("nombrePerfil")
                        )
                    }

                    Text(
                        text = stringResource(R.string.arroba_usuario, username),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.testTag("usernamePerfil")
                    )
                }
            }

            // Los contadores en una fila: el numero grande y la etiqueta pequena. Tocar uno abre esa lista
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                ProfileCounter(
                    numero = seguidores,
                    etiqueta = stringResource(R.string.seguidores_label),
                    onClick = onSeguidoresClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("contadorSeguidores")
                )
                ProfileCounter(
                    numero = seguidos,
                    etiqueta = stringResource(R.string.seguidos_label),
                    onClick = onSeguidosClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("contadorSeguidos")
                )
            }

            if (bio.isNotEmpty()) {
                Text(
                    text = bio,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 14.dp)
                )
            }

            // Solo sale si puso su Instagram: una pastilla rosada para que se vea que es un enlace
            if (instagramUrl.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                        .clickable { uriHandler.openUri(instagramUrl) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
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
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }

            accion()
        }
    }
}

// Un contador del encabezado: el numero grande arriba y que cuenta abajo, en una superficie que deja
// ver que se puede tocar
@Composable
fun ProfileCounter(
    numero: Int,
    etiqueta: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.06f))
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = numero.toString(),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = etiqueta,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
        )
    }
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
            fotoEditable = null,
            accion = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ProfileCounterPreview() {
    MixtappTheme(darkTheme = true) {
        ProfileCounter(
            numero = LocalPreviewProvider.usuario.seguidores,
            etiqueta = stringResource(R.string.seguidores_label),
            onClick = {}
        )
    }
}
