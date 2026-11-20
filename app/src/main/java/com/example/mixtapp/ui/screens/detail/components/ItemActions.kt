package com.example.mixtapp.ui.screens.detail.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppButton
import com.example.mixtapp.ui.utils.rebote

// Las acciones del item: calificarlo (o editar mi calificacion) como accion principal, debajo lo que
// la pantalla quiera destacar (los datos curiosos) y despues tres acciones con su icono y su etiqueta: me gusta, agregar a una mixtape y guardar. Cada una ocupa un
// area amplia y, cuando esta activa, se rellena en rosado. El corazon y el marcador cambian cuando
// llega el item por el listener
@Composable
fun ItemActions(
    // Si ya escribi mi resena, el boton la edita
    yaCalificado: Boolean,
    likesText: String,
    isLiked: Boolean,
    isSaved: Boolean,
    onRateClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onAddToMixtapeClick: () -> Unit,
    modifier: Modifier = Modifier,
    // Lo que va justo debajo de Calificar
    destacado: @Composable () -> Unit,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AppButton(
            texto = stringResource(if (yaCalificado) R.string.editar_calificacion else R.string.calificar),
            onClick = onRateClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("btnCalificar"),
            cargando = false,
        )

        Spacer(modifier = Modifier.height(12.dp))

        destacado()

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AccionDelItem(
                icono = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                // El contador va en la etiqueta: "Me gusta · 14K"
                etiqueta = "${stringResource(R.string.like_item)} · $likesText",
                activa = isLiked,
                descripcion = stringResource(if (isLiked) R.string.quitar_like else R.string.like_item),
                onClick = onLikeClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("btnLikeItem"),
            )

            // Abre la pantalla para elegir en cual de mis mixtapes va
            AccionDelItem(
                icono = Icons.AutoMirrored.Filled.PlaylistAdd,
                etiqueta = stringResource(R.string.mixtape_accion),
                activa = false,
                descripcion = stringResource(R.string.agregar_a_mixtape),
                onClick = onAddToMixtapeClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("btnAgregarAMixtape"),
            )

            // Lo guardado aparece en la pestana Guardados del perfil
            AccionDelItem(
                icono = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                etiqueta = stringResource(if (isSaved) R.string.guardado else R.string.save),
                activa = isSaved,
                descripcion = stringResource(if (isSaved) R.string.quitar_de_guardados else R.string.save),
                onClick = onSaveClick,
                modifier = Modifier
                    .weight(1f)
                    .testTag("btnGuardar"),
            )
        }
    }
}

// Una accion con su icono arriba y su etiqueta abajo. Activa, el fondo y el icono van en rosado;
// inactiva, en la superficie de las tarjetas
@Composable
private fun AccionDelItem(
    icono: ImageVector,
    etiqueta: String,
    activa: Boolean,
    descripcion: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colores = MaterialTheme.colorScheme
    val fondo = if (activa) colores.primary.copy(alpha = 0.2f) else colores.surfaceContainerHigh.copy(alpha = 0.6f)
    val color = if (activa) colores.primary else colores.onSurface

    Column(
        modifier = modifier
            .rebote()
            .clip(RoundedCornerShape(18.dp))
            .background(fondo)
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = icono,
            contentDescription = descripcion,
            tint = color,
            modifier = Modifier.size(24.dp),
        )
        Text(
            text = etiqueta,
            color = color,
            style = MaterialTheme.typography.labelLarge,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp, start = 4.dp, end = 4.dp),
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ItemActionsPreview() {
    val item = LocalPreviewProvider.fromZero

    MixtappTheme(darkTheme = true) {
        ItemActions(
            yaCalificado = false,
            likesText = item.likesText,
            isLiked = item.isLiked,
            isSaved = true,
            onRateClick = {},
            onLikeClick = {},
            onSaveClick = {},
            onAddToMixtapeClick = {},
            destacado = {},
        )
    }
}
