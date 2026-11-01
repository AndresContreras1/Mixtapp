package com.example.mixtapp.ui.screens.detail

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.ui.screens.detail.components.DatosCuriososButton
import com.example.mixtapp.ui.screens.detail.components.ItemActions
import com.example.mixtapp.ui.screens.detail.components.ItemHeader
import com.example.mixtapp.ui.screens.detail.components.ItemStats
import com.example.mixtapp.ui.screens.detail.components.MyRatingSection
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ReviewCard
import com.example.mixtapp.ui.utils.SectionTitle

// El detalle de un album, una cancion o un artista: recibe el tipo y el id, y el ViewModel se
// encarga de escuchar el item
@Composable
fun DetailScreen(
    tipo: String,
    itemId: String,
    detailViewModel: DetailViewModel,
    onRateClick: () -> Unit,
    onDatosCuriososClick: () -> Unit,
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

    DetailScreenContent(
        isLoading = state.isLoading,
        itemConResenas = state.itemConResenas,
        errorMessageRes = state.errorMessageRes,
        errorAccionRes = state.errorAccionRes,
        onRateClick = onRateClick,
        onLikeClick = { detailViewModel.sendOrDeleteLikeItem() },
        onSaveClick = { detailViewModel.sendOrDeleteGuardado() },
        onDatosCuriososClick = onDatosCuriososClick,
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
    onRateClick: () -> Unit,
    onLikeClick: () -> Unit,
    onSaveClick: () -> Unit,
    onDatosCuriososClick: () -> Unit,
    onReviewLikeClick: (String) -> Unit,
    onDeleteMyReviewClick: (String) -> Unit,
    onBackClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            isLoading -> LoadingBox()
            // Sin item no hay nada que mostrar: el error ocupa la pantalla
            itemConResenas == null -> ErrorBox(messageRes = errorMessageRes ?: R.string.album_no_encontrado)
            else -> {
                val item = itemConResenas.item

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) {
                    item {
                        ItemHeader(item = item, onBackClick = onBackClick)
                    }

                    item {
                        ItemStats(
                            rating = itemConResenas.rating,
                            estrellas = itemConResenas.estrellas,
                            ratingCount = itemConResenas.ratingCount,
                            recommendRate = itemConResenas.recommendRate,
                            likesText = item.likesText,
                            modifier = Modifier.padding(top = 20.dp),
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(top = 16.dp)) {
                            ItemActions(
                                // Solo hay una resena por item: si ya existe, se edita
                                yaCalificado = itemConResenas.reviews.any { it.isMine },
                                likesText = item.likesText,
                                isLiked = item.isLiked,
                                isSaved = item.isSaved,
                                onRateClick = onRateClick,
                                onLikeClick = onLikeClick,
                                onSaveClick = onSaveClick,
                            )

                            ErrorMessage(
                                messageRes = errorAccionRes,
                                modifier = Modifier.padding(top = 8.dp),
                            )
                        }
                    }

                    // No todos los items tienen datos curiosos
                    if (item.datosCuriosos.isNotEmpty()) {
                        item {
                            DatosCuriososButton(
                                onClick = onDatosCuriososClick,
                                modifier = Modifier.padding(top = 16.dp),
                            )
                        }
                    }

                    item {
                        MyRatingSection(
                            userRating = itemConResenas.userRating,
                            onClick = onRateClick,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    }

                    item {
                        SectionTitle(
                            title = stringResource(R.string.reviews),
                            modifier = Modifier.padding(top = 28.dp, bottom = 12.dp),
                        )
                    }

                    if (itemConResenas.reviews.isEmpty()) {
                        item {
                            EmptyMessage(
                                messageRes = R.string.sin_resenas_album,
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
                            mostrarItem = false,
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
            onRateClick = {},
            onLikeClick = {},
            onSaveClick = {},
            onDatosCuriososClick = {},
            onReviewLikeClick = {},
            onDeleteMyReviewClick = {},
            onBackClick = {},
            onAuthorClick = {},
            onCommentsClick = {},
        )
    }
}
