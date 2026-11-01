package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.ui.theme.MixtappTheme
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
            title = stringResource(if (item.youtubeUrl.isNotEmpty()) R.string.video_original else R.string.escuchalo)
        )

        if (item.youtubeUrl.isNotEmpty()) {
            EnlaceOvalado(
                texto = stringResource(R.string.ver_en_youtube),
                onClick = { uriHandler.openUri(item.youtubeUrl) },
                modifier = Modifier.testTag("btnYoutube"),
            )
        }

        if (item.spotifyUrl.isNotEmpty()) {
            EnlaceOvalado(
                texto = stringResource(R.string.escuchar_en_spotify),
                onClick = { uriHandler.openUri(item.spotifyUrl) },
                modifier = Modifier.testTag("btnSpotify"),
            )
        }

        if (item.instagramUrl.isNotEmpty()) {
            EnlaceOvalado(
                texto = stringResource(R.string.enlace_instagram_artista, item.instagram),
                onClick = { uriHandler.openUri(item.instagramUrl) },
                modifier = Modifier.testTag("btnInstagram"),
            )
        }
    }
}

// Una tarjeta ovalada con el texto del link, igual a la de los datos curiosos
@Composable
private fun EnlaceOvalado(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = CircleShape,
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun EnlacesDelItemPreview() {
    MixtappTheme(darkTheme = true) {
        EnlacesDelItem(item = LocalPreviewProvider.emptinessMachine)
    }
}
