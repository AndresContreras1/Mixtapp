package com.example.mixtapp.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.ui.screens.search.components.BrowseBySection
import com.example.mixtapp.ui.screens.search.components.SearchHeader
import com.example.mixtapp.ui.screens.search.model.SearchCategoryUi
import com.example.mixtapp.ui.theme.DeepBackground
import com.example.mixtapp.ui.theme.MixtappTheme

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    modifier: Modifier = Modifier,
) {
    val state by searchViewModel.uiState.collectAsState()

    SearchScreenContent(
        query = state.query,
        onQueryChange = { searchViewModel.updateQuery(query = it) },
        categories = state.categories,
        selectedCategoryId = state.selectedCategoryId,
        onCategoryClick = { searchViewModel.updateSelectedCategory(categoryId = it) },
        modifier = modifier,
    )
}

@Composable
fun SearchScreenContent(
    query: String,
    onQueryChange: (String) -> Unit,
    categories: List<SearchCategoryUi>,
    selectedCategoryId: String?,
    onCategoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ) {
                SearchHeader(
                    query = query,
                    onQueryChange = onQueryChange,
                )

                BrowseBySection(
                    categories = categories,
                    selectedCategoryId = selectedCategoryId,
                    onCategoryClick = onCategoryClick,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )

                Spacer(modifier = Modifier.height(28.dp))
            }

        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun SearchScreenPreview() {
    MixtappTheme(darkTheme = true, dynamicColor = false) {
        SearchScreenContent(
            query = "",
            onQueryChange = {},
            categories = LocalSearchCategoriesProvider.categories,
            selectedCategoryId = null,
            onCategoryClick = {},
        )
    }
}
