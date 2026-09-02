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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewCard
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewsFilter
import com.example.mixtapp.ui.screens.myreviews.components.MyReviewsHeader
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewUi
import com.example.mixtapp.ui.theme.DeepBackground

@Composable
fun MyReviewsScreen(
    myReviewsViewModel: MyReviewsViewModel,
    onReviewClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val state by myReviewsViewModel.uiState.collectAsState()

    MyReviewsScreenContent(
        username = state.username,
        joinDate = state.joinDate,
        reviews = state.reviews,
        selectedFilter = state.selectedFilter,
        onFilterSelected = { myReviewsViewModel.updateSelectedFilter(filtro = it) },
        onReviewClick = onReviewClick,
        modifier = modifier
    )
}

@Composable
fun MyReviewsScreenContent(
    username: String,
    joinDate: String,
    reviews: List<MyReviewUi>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    onReviewClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {
                MyReviewsHeader(
                    username = username,
                    joinDate = joinDate,
                    modifier = Modifier.padding(top = 24.dp)
                )
                MyReviewsFilter(
                    selected = selectedFilter,
                    onFilterSelected = onFilterSelected,
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
    MyReviewsScreenContent(
        username = "Yourname",
        joinDate = "march 2025",
        reviews = LocalMyReviewsProvider.reviews,
        selectedFilter = "Recent",
        onFilterSelected = {},
        onReviewClick = {}
    )
}
