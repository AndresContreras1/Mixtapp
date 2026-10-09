package com.example.mixtapp.ui.screens.myreviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.ui.components.ErrorMessage
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewCard
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewsFilter
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewsHeader
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewFilterUi
import com.example.mixtapp.ui.screens.myreviews.model.myReviewFilters
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun MyReviewsScreen(
    myReviewsViewModel: MyReviewsViewModel,
    onReviewClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by myReviewsViewModel.uiState.collectAsState()

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        else -> MyReviewsScreenContent(
            username = state.username,
            iniciales = state.iniciales,
            joinDate = state.joinDate,
            reviews = state.reviews,
            filters = state.filters,
            selectedFilterId = state.selectedFilterId,
            errorMessageRes = state.errorMessageRes,
            onFilterSelected = { myReviewsViewModel.updateSelectedFilter(filtroId = it) },
            onReviewClick = onReviewClick,
            onEditClick = onEditClick,
            onDeleteClick = { myReviewsViewModel.eliminarResena(reviewId = it) },
            modifier = modifier
        )
    }
}

@Composable
fun MyReviewsScreenContent(
    username: String,
    iniciales: String,
    joinDate: String,
    reviews: List<MyReviewUi>,
    filters: List<MyReviewFilterUi>,
    selectedFilterId: String,
    errorMessageRes: Int?,
    onFilterSelected: (String) -> Unit,
    onReviewClick: (String) -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
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
                    joinDate = joinDate,
                    modifier = Modifier.padding(top = 24.dp, bottom = 16.dp)
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
                items(reviews, key = { it.id }) { review ->
                    MyReviewCard(
                        review = review,
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
            joinDate = "march 2025",
            reviews = LocalMyReviewsProvider.reviews,
            filters = myReviewFilters,
            selectedFilterId = myReviewFilters.first().id,
            errorMessageRes = null,
            onFilterSelected = {},
            onReviewClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
