package com.example.mixtapp.ui.screens.myreviews

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ReviewCard
import com.example.mixtapp.ui.utils.ScreenTitle

@Composable
fun MyReviewsScreen(
    myReviewsViewModel: MyReviewsViewModel,
    onItemClick: (String, String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onEditClick: (String, String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by myReviewsViewModel.uiState.collectAsState()

    MyReviewsScreenContent(
        reviews = state.reviews,
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        errorAccionRes = state.errorAccionRes,
        onItemClick = { item -> onItemClick(item.tipo, item.id) },
        onCommentsClick = onCommentsClick,
        // Editar abre la pantalla de escribir con lo que se califico en la resena
        onEditClick = { review -> onEditClick(review.item.tipo, review.item.id) },
        onDeleteClick = { myReviewsViewModel.eliminarResena(reviewId = it) },
        onLikeClick = { myReviewsViewModel.sendOrDeleteLike(reviewId = it) },
        onBackClick = onBackClick,
        modifier = modifier
    )
}

@Composable
fun MyReviewsScreenContent(
    reviews: List<ReviewUi>,
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorAccionRes: Int?,
    onItemClick: (ItemResumenUi) -> Unit,
    onCommentsClick: (String) -> Unit,
    onEditClick: (ReviewUi) -> Unit,
    onDeleteClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // El titulo queda fijo para poder volver aunque falle la carga
            ScreenTitle(
                title = stringResource(R.string.my_reviews),
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            when {
                isLoading -> LoadingBox()
                // Sin resenas cargadas solo se muestra el motivo del error
                reviews.isEmpty() && errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
                else -> {
                    ErrorMessage(
                        messageRes = errorAccionRes,
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("listaMisResenas"),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    ) {
                        if (reviews.isEmpty()) {
                            item {
                                EmptyMessage(
                                    messageRes = R.string.sin_mis_resenas,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        items(reviews, key = { it.id }) { review ->
                            ReviewCard(
                                review = review,
                                // Ya estoy en mis resenas: tocar mi nombre no abre nada
                                onAuthorClick = {},
                                onLikeClick = onLikeClick,
                                onCommentsClick = onCommentsClick,
                                mostrarItem = true,
                                onItemClick = onItemClick,
                                onEditClick = onEditClick,
                                onDeleteClick = onDeleteClick,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun MyReviewsScreenPreview() {
    MixtappTheme(darkTheme = true) {
        MyReviewsScreenContent(
            reviews = listOf(LocalPreviewProvider.miReview),
            isLoading = false,
            errorMessageRes = null,
            errorAccionRes = null,
            onItemClick = {},
            onCommentsClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onLikeClick = {},
            onBackClick = {}
        )
    }
}
