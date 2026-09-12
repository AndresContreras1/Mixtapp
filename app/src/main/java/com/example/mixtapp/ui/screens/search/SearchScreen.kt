package com.example.mixtapp.ui.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.ui.components.ReviewAlbumRow
import com.example.mixtapp.ui.screens.search.components.BrowseBySection
import com.example.mixtapp.ui.screens.search.components.SearchHeader
import com.example.mixtapp.ui.screens.search.model.SearchCategoryUi
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi
import com.example.mixtapp.ui.theme.MixtappTheme
import kotlin.math.roundToInt

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by searchViewModel.uiState.collectAsState()

    SearchScreenContent(
        query = state.query,
        onQueryChange = { searchViewModel.updateQuery(query = it) },
        categories = state.categories,
        selectedCategoryId = state.selectedCategoryId,
        onCategoryClick = { searchViewModel.updateSelectedCategory(categoryId = it) },
        resultados = state.resultados,
        onAlbumClick = onAlbumClick,
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
    resultados: List<SongReviewUi>,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 28.dp),
        ) {
            item {
                SearchHeader(
                    query = query,
                    onQueryChange = onQueryChange,
                )
            }

            if (query.isBlank()) {
                item {
                    BrowseBySection(
                        categories = categories,
                        selectedCategoryId = selectedCategoryId,
                        onCategoryClick = onCategoryClick,
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                }
            } else if (resultados.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.sin_resultados),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                    )
                }
            } else {
                items(resultados, key = { it.id }) { album ->
                    ReviewAlbumRow(
                        cover = album.cover,
                        albumTitle = album.title,
                        artistName = album.artist,
                        rating = album.rating.roundToInt(),
                        coverSize = 64.dp,
                        coverCorner = 10.dp,
                        starSize = 14.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onAlbumClick(album.id) }
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                    )
                }
            }

            item {
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
            resultados = emptyList(),
            onAlbumClick = {},
        )
    }
}
