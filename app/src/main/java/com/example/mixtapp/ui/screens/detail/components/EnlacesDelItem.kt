package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.OpcionOvalada
import com.example.mixtapp.ui.utils.SectionTitle

// Para escucharlo o verlo afuera de la app: el video original en YouTube (solo canciones), Spotify
// y el Instagram del artista. LocalUriHandler abre el link en el navegador o en la app que lo
// tenga. Un link vacio no se muestra
@Composable
fun EnlacesDelItem(
    item: ItemUi,
    modifier: Modifier = Modifier,
) {
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        // Las canciones tienen video; los albumes y los artistas solo se escuchan o se siguen
        SectionTitle(
            title = stringResource(if (item.youtubeUrl.isNotEmpty()) R.string.video_original else R.string.escuchalo),
            actionText = null,
            onActionClick = null,
        )

        if (item.youtubeUrl.isNotEmpty()) {
            OpcionOvalada(
                icono = null,
                texto = stringResource(R.string.ver_en_youtube),
                onClick = { uriHandler.openUri(item.youtubeUrl) },
                modifier = Modifier.testTag("btnYoutube"),
            )
        }

        if (item.spotifyUrl.isNotEmpty()) {
            OpcionOvalada(
                icono = null,
                texto = stringResource(R.string.escuchar_en_spotify),
                onClick = { uriHandler.openUri(item.spotifyUrl) },
                modifier = Modifier.testTag("btnSpotify"),
            )
        }

        if (item.instagramUrl.isNotEmpty()) {
            OpcionOvalada(
                icono = null,
                texto = stringResource(R.string.enlace_instagram_artista, item.instagram),
                onClick = { uriHandler.openUri(item.instagramUrl) },
                modifier = Modifier.testTag("btnInstagram"),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun EnlacesDelItemPreview() {
    MixtappTheme(darkTheme = true) {
        EnlacesDelItem(item = LocalPreviewProvider.emptinessMachine)
    }
}
