package com.example.mixtapp.ui.screens.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.ui.screens.detail.components.DatosCuriososButton
import com.example.mixtapp.ui.screens.detail.components.EnlacesDelItem
import com.example.mixtapp.ui.screens.detail.components.ItemActions
import com.example.mixtapp.ui.screens.detail.components.ItemStats
import com.example.mixtapp.ui.screens.detail.components.SonandoButton
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.ItemHeader
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ReviewCard
import com.example.mixtapp.ui.utils.SectionTitle
import com.example.mixtapp.ui.utils.TonoDeFondo

// El detalle de un album, una cancion o un artista: recibe el tipo y el id, y el ViewModel se
// encarga de escuchar el item
@Composable
fun DetailScreen(
    tipo: String,
    itemId: String,
    detailViewModel: DetailViewModel,
    onRateClick: () -> Unit,
    onDatosCuriososClick: () -> Unit,
    onAddToMixtapeClick: () -> Unit,
    onBackClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by detailViewModel.uiState.collectAsState()

    // Necesita el tipo y el id que llegan por la ruta, asi que empieza a escuchar en un
    // LaunchedEffect. Al volver de calificar no hace falta pedir nada: el flujo ya trae mi resena
    LaunchedEffect(Unit) {
        detailViewModel.getItem(tipo = tipo, itemId = itemId)
    }

    // Al salir de la pantalla (a calificar, a otra pestana o atras) el fragmento deja de sonar
    DisposableEffect(Unit) {
        onDispose { detailViewModel.detenerVistaPrevia() }
    }

    DetailScreenContent(
        isLoading = state.isLoading,
        itemConResenas = state.itemConResenas,
        errorMessageRes = state.errorMessageRes,
        errorAccionRes = state.errorAccionRes,
        avisoSonandoRes = state.avisoSonandoRes,
        estadoVistaPrevia = state.vistaPreviaEstado,
        errorVistaPreviaRes = state.errorVistaPreviaRes,
        onRateClick = onRateClick,
        onLikeClick = { detailViewModel.sendOrDeleteLikeItem() },
        onSaveClick = { detailViewModel.sendOrDeleteGuardado() },
        onAddToMixtapeClick = onAddToMixtapeClick,
        onDatosCuriososClick = onDatosCuriososClick,
        onSonandoClick = { detailViewModel.ponerSonando() },
        onPlayClick = { detailViewModel.alternarVistaPreviaDelItem() },
        onReviewLikeClick = { detailViewModel.sendOrDeleteLikeResena(reviewId = it) },
        onDeleteMyReviewClick = { detailViewModel.eliminarMiResena(reviewId = it) },
        onBackClick = onBackClick,
        onAuthorClick = onAuthorClick,
        onCommentsClick = onCommentsClick,
        modifier = modifier,
    )
}

@Composable
fun DetailScreenContent(
    isLoading: Boolean,
    itemConResenas: ItemConResenasUi?,
    @StringRes errorMessageRes: Int?,
    @StringRes errorAccionRes: Int?,
    @StringRes avisoSonandoRes: Int?,
    estadoVistaPrevia: EstadoDeReproduccion?,
    @StringRes errorVistaPreviaRes: Int?,
    onRateClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onAddToMixtapeClick: () -> Unit,
    onDatosCuriososClick: () -> Unit,
    onSonandoClick: () -> Unit,
    onPlayClick: () -> Unit,
    onReviewLikeClick: (String) -> Unit,
    onDeleteMyReviewClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    // El color de la portada solo decora: lo avisa la imagen al cargar y pinta el degradado de
    // arriba. Es de la vista, no del estado, porque no cambia ningun dato
    var colorDePortada by remember { mutableStateOf<Color?>(null) }

    Box(modifier = modifier.fillMaxSize().testTag("detailScreen")) {
        AppBackground(tono = TonoDeFondo.Item)

        // El degradado con el color de la portada, detras de la imagen y desvanecido hacia abajo
        val color = colorDePortada
        if (color != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.55f), Color.Transparent)))
            )
        }

        when {
            isLoading -> LoadingBox()
            // Sin item no hay nada que mostrar: el error ocupa la pantalla
            itemConResenas == null -> ErrorBox(messageRes = errorMessageRes ?: R.string.item_no_encontrado)
            else -> {
                val item = itemConResenas.item

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("listaDetalle"),
                    contentPadding = PaddingValues(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 32.dp),
                ) {
                    item {
                        // Si el listener falla despues de cargar, el aviso sale arriba y queda lo de antes
                        ErrorMessage(messageRes = errorMessageRes)
                        ItemHeader(
                            item = item,
                            onBackClick = onBackClick,
                            onColorDePortada = { colorDePortada = it },
                        )
                    }

                    item {
                        ItemStats(
                            rating = itemConResenas.rating,
                            estrellas = itemConResenas.estrellas,
                            ratingCount = itemConResenas.ratingCount,
                            recommendRate = itemConResenas.recommendRate,
                            recomiendan = itemConResenas.recomiendan,
                            modifier = Modifier.padding(top = 20.dp),
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            ItemActions(
                                // Solo hay una resena por item: si ya existe, se edita
                                yaCalificado = itemConResenas.yaCalificado,
                                likesText = item.likesText,
                                isLiked = item.isLiked,
                                isSaved = item.isSaved,
                                onRateClick = onRateClick,
                                onLikeClick = onLikeClick,
                                onSaveClick = onSaveClick,
                                onAddToMixtapeClick = onAddToMixtapeClick,
                            )

                            ErrorMessage(
                                messageRes = errorAccionRes,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }

                    // Bloque 1, escuchar y ver: el fragmento en la app y los enlaces para seguir afuera
                    item {
                        EnlacesDelItem(
                            item = item,
                            estadoVistaPrevia = estadoVistaPrevia,
                            errorVistaPreviaRes = errorVistaPreviaRes,
                            onPlayClick = onPlayClick,
                            modifier = Modifier.padding(top = 28.dp),
                        )
                    }

                    // Bloque 2, descubre mas: los datos curiosos (su pantalla avisa si el item no
                    // tiene) y contarles a mis seguidores que lo escucho
                    item {
                        Column(modifier = Modifier.padding(top = 28.dp)) {
                            SectionTitle(
                                title = stringResource(R.string.descubre_mas),
                                modifier = Modifier.padding(bottom = 10.dp),
                                actionText = null,
                                onActionClick = null,
                            )

                            DatosCuriososButton(onClick = onDatosCuriososClick)

                            // Lo ven mis seguidores en Siguiendo; el aviso confirma que ya quedo
                            SonandoButton(
                                onClick = onSonandoClick,
                                modifier = Modifier.padding(top = 10.dp),
                            )

                            if (avisoSonandoRes != null) {
                                Text(
                                    text = stringResource(avisoSonandoRes),
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier
                                        .padding(top = 8.dp, start = 20.dp)
                                        .testTag("avisoSonando"),
                                )
                            }
                        }
                    }

                    // Bloque 3, las resenas de la comunidad
                    item {
                        SectionTitle(
                            title = stringResource(R.string.resenas_de_la_comunidad),
                            modifier = Modifier.padding(top = 28.dp, bottom = 12.dp),
                            actionText = null,
                            onActionClick = null,
                        )
                    }

                    if (itemConResenas.reviews.isEmpty()) {
                        item {
                            EmptyMessage(
                                messageRes = R.string.sin_resenas_item,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }

                    // Ya llegan de la ultima publicada o editada a la mas vieja. El item no se
                    // repite en cada tarjeta: ya esta arriba
                    items(itemConResenas.reviews, key = { it.id }) { review ->
                        ReviewCard(
                            review = review,
                            onAuthorClick = onAuthorClick,
                            onLikeClick = onReviewLikeClick,
                            onCommentsClick = onCommentsClick,
                            onItemClick = null,
                            // Editar o eliminar solo aparecen en mi resena
                            onEditClick = { onRateClick() },
                            onDeleteClick = onDeleteMyReviewClick,
                            modifier = Modifier.padding(bottom = 12.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun DetailScreenPreview() {
    MixtappTheme(darkTheme = true) {
        DetailScreenContent(
            isLoading = false,
            itemConResenas = LocalPreviewProvider.itemConResenas,
            errorMessageRes = null,
            errorAccionRes = null,
            avisoSonandoRes = null,
            estadoVistaPrevia = null,
            errorVistaPreviaRes = null,
            onRateClick = {},
            onLikeClick = {},
            onSaveClick = {},
            onAddToMixtapeClick = {},
            onDatosCuriososClick = {},
            onSonandoClick = {},
            onPlayClick = {},
            onReviewLikeClick = {},
            onDeleteMyReviewClick = {},
            onBackClick = {},
            onAuthorClick = {},
            onCommentsClick = {},
        )
    }
}
