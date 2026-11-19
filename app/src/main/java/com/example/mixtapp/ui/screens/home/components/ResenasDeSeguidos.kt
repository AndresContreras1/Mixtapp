package com.example.mixtapp.ui.screens.home.components

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.ReviewCard
import com.example.mixtapp.ui.utils.SectionTitle
import com.example.mixtapp.ui.utils.TarjetaTranslucida

// Las resenas de quienes sigo, lo que hace a Mixtapp distinta de un catalogo: va arriba en el inicio,
// justo despues de la tendencia, en una fila que se desliza. Sin resenas no queda un hueco: un
// mensaje claro y el boton para descubrir personas
@Composable
fun ResenasDeSeguidos(
    resenas: List<ReviewUi>,
    @StringRes errorAccionRes: Int?,
    onAuthorClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onItemClick: (ItemResumenUi) -> Unit,
    onDescubrirPersonasClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(
            title = stringResource(R.string.resenas_de_seguidos),
            actionText = null,
            onActionClick = null,
        )

        // El error del like se ve junto a las tarjetas donde se toco
        ErrorMessage(messageRes = errorAccionRes)

        if (resenas.isEmpty()) {
            SinResenasDeSeguidos(
                onDescubrirPersonasClick = onDescubrirPersonasClick,
                modifier = Modifier.padding(top = 8.dp),
            )
        } else {
            // La fila llega hasta los bordes de la pantalla: la tarjeta cortada a la derecha avisa
            // que hay mas
            LazyRow(
                modifier = Modifier.padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp),
            ) {
                items(resenas, key = { it.id }) { review ->
                    ReviewCard(
                        review = review,
                        onAuthorClick = onAuthorClick,
                        onLikeClick = onLikeClick,
                        onCommentsClick = onCommentsClick,
                        onItemClick = onItemClick,
                        onEditClick = null,
                        onDeleteClick = null,
                        modifier = Modifier.width(300.dp),
                    )
                }
            }
        }
    }
}

// El estado vacio: que pasa y que hacer, en vez de un espacio sin contenido
@Composable
private fun SinResenasDeSeguidos(
    onDescubrirPersonasClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TarjetaTranslucida(onClick = null, modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Outlined.PersonSearch,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(36.dp),
            )
            Text(
                text = stringResource(R.string.sin_actividad_seguidos),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp, bottom = 14.dp),
            )
            OutlinedButton(
                onClick = onDescubrirPersonasClick,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                modifier = Modifier.testTag("btnDescubrirPersonas"),
            ) {
                Text(
                    text = stringResource(R.string.descubrir_personas),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun ResenasDeSeguidosPreview() {
    MixtappTheme(darkTheme = true) {
        ResenasDeSeguidos(
            resenas = listOf(LocalPreviewProvider.review),
            errorAccionRes = null,
            onAuthorClick = {},
            onLikeClick = {},
            onCommentsClick = {},
            onItemClick = {},
            onDescubrirPersonasClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ResenasDeSeguidosVaciaPreview() {
    MixtappTheme(darkTheme = true) {
        ResenasDeSeguidos(
            resenas = emptyList(),
            errorAccionRes = null,
            onAuthorClick = {},
            onLikeClick = {},
            onCommentsClick = {},
            onItemClick = {},
            onDescubrirPersonasClick = {},
        )
    }
}
