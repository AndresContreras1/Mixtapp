package com.example.mixtapp.ui.screens.myreviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewsFilter
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewsHeader
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewFilterUi
import com.example.mixtapp.ui.screens.myreviews.model.myReviewFilters
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.MyReviewCard

@Composable
fun MyReviewsScreen(
    myReviewsViewModel: MyReviewsViewModel,
    onReviewClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by myReviewsViewModel.uiState.collectAsState()

    // Se pide cada vez que se muestra: al volver de editar una resena, ya sale cambiada
    LaunchedEffect(Unit) {
        myReviewsViewModel.getReviews()
    }

    when {
        state.isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // Sin resenas cargadas solo se muestra el motivo del error
        state.errorMessageRes != null && state.todas.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.error_cargar_contenido))
            }
        }
        else -> MyReviewsScreenContent(
            username = state.username,
            iniciales = state.iniciales,
            reviewsCount = state.todas.size,
            reviews = state.reviews,
            filters = state.filters,
            selectedFilterId = state.selectedFilterId,
            errorMessageRes = state.errorMessageRes,
            onFilterSelected = { myReviewsViewModel.updateSelectedFilter(filtroId = it) },
            onReviewClick = onReviewClick,
            // Editar abre la pantalla de escribir con el album de la resena
            onEditClick = { onEditClick(it.album.id) },
            onDeleteClick = { myReviewsViewModel.eliminarResena(reviewId = it) },
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}

@Composable
fun MyReviewsScreenContent(
    username: String,
    iniciales: String,
    reviewsCount: Int,
    reviews: List<MyReviewUi>,
    filters: List<MyReviewFilterUi>,
    selectedFilterId: String,
    errorMessageRes: Int?,
    onFilterSelected: (String) -> Unit,
    onReviewClick: (String) -> Unit,
    onEditClick: (MyReviewUi) -> Unit,
    onDeleteClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                MyReviewsHeader(
                    username = username,
                    iniciales = iniciales,
                    reviewsCount = reviewsCount,
                    onBackClick = onBackClick,
                    modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)
                )
                MyReviewsFilter(
                    filters = filters,
                    selectedId = selectedFilterId,
                    onFilterSelected = onFilterSelected,
                )
                ErrorMessage(
                    messageRes = errorMessageRes,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            ) {
                if (reviews.isEmpty()) {
                    item {
                        Text(
                            text = stringResource(R.string.sin_mis_resenas),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }

                items(reviews.size) { index ->
                    MyReviewCard(
                        review = reviews[index],
                        onReviewClick = onReviewClick,
                        showEditButton = true,
                        onEditClick = onEditClick,
                        onDeleteClick = onDeleteClick,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
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
            username = "Yourname",
            iniciales = "YO",
            reviewsCount = LocalMyReviewsProvider.reviews.size,
            reviews = LocalMyReviewsProvider.reviews,
            filters = myReviewFilters,
            selectedFilterId = myReviewFilters.first().id,
            errorMessageRes = null,
            onFilterSelected = {},
            onReviewClick = {},
            onEditClick = {},
            onDeleteClick = {},
            onBackClick = {}
        )
    }
}
