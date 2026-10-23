package com.example.mixtapp.ui.screens.userprofile.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalUsuarioProvider
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.BackButton
import com.example.mixtapp.ui.utils.ProfileAsyncImage

@Composable
fun UserProfileHeader(
    usuario: UsuarioUi,
    onSeguidoresClick: () -> Unit,
    onSeguidosClick: () -> Unit,
    onFollowClick: () -> Unit,
    reviewsCount: Int,
    // Mientras llegan las resenas no se muestra el total
    cargandoResenas: Boolean,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            BackButton(onBackClick = onBackClick)
        }

        ProfileAsyncImage(
            profileImage = usuario.fotoUrl,
            contentDescription = stringResource(R.string.foto_perfil),
            modifier = Modifier.size(96.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = usuario.nombre,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = stringResource(R.string.arroba_usuario, usuario.username),
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            fontSize = 13.sp
        )

        if (usuario.bio.isNotEmpty()) {
            Text(
                text = usuario.bio,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tocar un contador abre esa lista
            Text(
                text = stringResource(R.string.cantidad_seguidores, usuario.seguidores),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSeguidoresClick() }
            )
            Text(
                text = stringResource(R.string.cantidad_seguidos, usuario.seguidos),
                color = MaterialTheme.colorScheme.onBackground,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onSeguidosClick() }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!usuario.esMiPerfil) {
            Button(onClick = onFollowClick) {
                Text(text = stringResource(if (usuario.siguiendo) R.string.dejar_de_seguir else R.string.seguir_usuario))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (!cargandoResenas) {
            Text(
                text = if (reviewsCount == 1) {
                    stringResource(R.string.una_resena)
                } else {
                    stringResource(R.string.n_resenas, reviewsCount)
                },
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun UserProfileHeaderPreview() {
    MixtappTheme(darkTheme = true) {
        UserProfileHeader(
            usuario = LocalUsuarioProvider.usuario,
            onSeguidoresClick = {},
            onSeguidosClick = {},
            onFollowClick = {},
            reviewsCount = 3,
            cargandoResenas = false,
            onBackClick = {},
        )
    }
}
