package com.example.mixtapp.ui.screens.mapa.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ResenaEnMapaUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.ReviewCard

// Lo que sale abajo del mapa al tocar un marcador: la tarjeta de la resena, con la X para cerrarla y
// el error si fallo el like
@Composable
fun TarjetaDeResenaDelMapa(
    resena: ResenaEnMapaUi,
    @StringRes errorAccionRes: Int?,
    onCerrarClick: () -> Unit,
    onLikeClick: (String) -> Unit,
    onItemClick: (ItemResumenUi) -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .testTag("tarjetaMapa"),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
        ) {
            IconButton(
                onClick = onCerrarClick,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surfaceContainer),
            ) {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(R.string.cerrar_resena_del_mapa),
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        ErrorMessage(messageRes = errorAccionRes)

        ReviewCard(
            review = resena.review,
            onAuthorClick = onAuthorClick,
            onLikeClick = onLikeClick,
            onCommentsClick = onCommentsClick,
            onItemClick = onItemClick,
            onEditClick = null,
            onDeleteClick = null,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TarjetaDeResenaDelMapaPreview() {
    MixtappTheme(darkTheme = true) {
        TarjetaDeResenaDelMapa(
            resena = ResenaEnMapaUi(review = LocalPreviewProvider.review, latitud = 4.65, longitud = -74.06),
            errorAccionRes = null,
            onCerrarClick = {},
            onLikeClick = {},
            onItemClick = {},
            onAuthorClick = {},
            onCommentsClick = {},
        )
    }
}
