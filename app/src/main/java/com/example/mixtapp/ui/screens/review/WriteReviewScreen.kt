package com.example.mixtapp.ui.screens.review

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MAX_REVIEW_LENGTH
import com.example.mixtapp.ui.screens.review.components.ReviewFormButtons
import com.example.mixtapp.ui.screens.review.components.ReviewItemCard
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppTextArea
import com.example.mixtapp.ui.utils.AppTextField
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.StarRating

// Escribir o editar mi resena de un album, una cancion o un artista. Recibe el tipo y el id; el
// ViewModel se encarga de buscar el item y mi resena si ya existe
@Composable
fun WriteReviewScreen(
    tipo: String,
    itemId: String,
    writeReviewViewModel: WriteReviewViewModel,
    onPublicada: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by writeReviewViewModel.uiState.collectAsState()

    // Se vuelve atras solo cuando la base confirma que la resena quedo guardada
    LaunchedEffect(state.navigate) {
        if (state.navigate) {
            onPublicada()
        }
    }

    // Necesita el tipo y el id que llegan por la ruta
    LaunchedEffect(Unit) {
        writeReviewViewModel.getItem(tipo = tipo, itemId = itemId)
    }

    WriteReviewScreenContent(
        isLoading = state.isLoading,
        item = state.item,
        editando = state.reviewId != null,
        rating = state.rating,
        reviewText = state.reviewText,
        listenedDate = state.listenedDate,
        publicando = state.publicando,
        errorMessageRes = state.errorMessageRes,
        onCancel = onCancel,
        onRatingChange = { writeReviewViewModel.updateRating(rating = it) },
        onReviewChange = { writeReviewViewModel.updateReviewText(reviewText = it) },
        onDateChange = { writeReviewViewModel.updateListenedDate(listenedDate = it) },
        onPostClick = { writeReviewViewModel.publicarResena() },
        modifier = modifier,
    )
}

@Composable
fun WriteReviewScreenContent(
    isLoading: Boolean,
    item: ItemUi?,
    // La misma pantalla crea y edita: solo cambia el titulo
    editando: Boolean,
    rating: Int,
    reviewText: String,
    listenedDate: String,
    publicando: Boolean,
    @StringRes errorMessageRes: Int?,
    onCancel: () -> Unit,
    onRatingChange: (Int) -> Unit,
    onReviewChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        when {
            isLoading -> LoadingBox()
            item == null -> ErrorBox(messageRes = errorMessageRes ?: R.string.album_no_encontrado)
            // Los botones quedan fijos abajo y suben con el teclado; el formulario se desplaza
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
                ) {
                    item {
                        ScreenTitle(
                            title = stringResource(if (editando) R.string.editar_resena else R.string.write_review),
                            onBackClick = onCancel,
                        )
                    }

                    item {
                        ReviewItemCard(
                            item = item,
                            modifier = Modifier.padding(top = 16.dp),
                        )
                    }

                    item {
                        Column(modifier = Modifier.padding(top = 24.dp)) {
                            Text(
                                text = stringResource(R.string.your_rating),
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Tocar la misma estrella otra vez la quita
                            StarRating(
                                rating = rating,
                                starSize = 36.dp,
                                onRatingChange = onRatingChange,
                                modifier = Modifier.testTag("estrellasResena"),
                            )
                        }
                    }

                    item {
                        AppTextField(
                            value = listenedDate,
                            onValueChange = onDateChange,
                            placeholder = stringResource(R.string.when_did_you_listen),
                            label = stringResource(R.string.label_fecha_escucha),
                            leadingIcon = Icons.Outlined.CalendarMonth,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .padding(top = 24.dp)
                                .testTag("txtFecha"),
                        )
                    }

                    item {
                        AppTextArea(
                            value = reviewText,
                            onValueChange = onReviewChange,
                            label = stringResource(R.string.label_resena),
                            placeholder = stringResource(R.string.review_placeholder),
                            maxLength = MAX_REVIEW_LENGTH,
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .testTag("txtResena"),
                        )
                    }

                    item {
                        ErrorMessage(
                            messageRes = errorMessageRes,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                    }
                }

                ReviewFormButtons(
                    publicando = publicando,
                    onCancel = onCancel,
                    onPostClick = onPostClick,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun WriteReviewScreenPreview() {
    MixtappTheme(darkTheme = true) {
        WriteReviewScreenContent(
            isLoading = false,
            item = LocalPreviewProvider.fromZero,
            editando = false,
            rating = 4,
            reviewText = "",
            listenedDate = "",
            publicando = false,
            errorMessageRes = null,
            onCancel = {},
            onRatingChange = {},
            onReviewChange = {},
            onDateChange = {},
            onPostClick = {},
        )
    }
}
