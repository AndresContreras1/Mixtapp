package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ProfileAsyncImage

// La fila de arriba con las fotos de los usuarios que sigo: tocar uno abre su perfil
@Composable
fun FollowingUsersRow(
    seguidos: List<UsuarioUi>,
    onUserClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 20.dp),
    ) {
        items(seguidos, key = { it.id }) { usuario ->
            Column(
                // Con un ancho fijo los nombres largos no corren a los demas
                modifier = Modifier
                    .width(68.dp)
                    .clickable { onUserClick(usuario.id) },
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                ProfileAsyncImage(
                    profileImage = usuario.fotoUrl,
                    contentDescription = stringResource(R.string.foto_perfil),
                    modifier = Modifier.size(58.dp)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = usuario.nombre,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun FollowingUsersRowPreview() {
    MixtappTheme(darkTheme = true) {
        FollowingUsersRow(
            seguidos = LocalPreviewProvider.following.seguidos,
            onUserClick = {},
        )
    }
}
