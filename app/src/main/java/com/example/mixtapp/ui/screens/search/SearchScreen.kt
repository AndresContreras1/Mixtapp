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
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.ui.screens.search.components.BrowseByHeader
import com.example.mixtapp.ui.screens.search.components.SearchCategoryRow
import com.example.mixtapp.ui.screens.search.components.SearchHeader
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.ReviewAlbumRow

@Composable
fun SearchScreen(
    searchViewModel: SearchViewModel,
    onAlbumClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by searchViewModel.uiState.collectAsState()

    // Se pide cada vez que se muestra: al volver de escribir una resena, el promedio ya sale al dia
    LaunchedEffect(Unit) {
        searchViewModel.getAlbumes()
    }

    when {
        state.isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // Sin albumes cargados solo se muestra el motivo del error
        state.errorMessageRes != null && state.todos.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.error_cargar_contenido))
            }
        }
        else -> SearchScreenContent(
            query = state.query,
            onQueryChange = { searchViewModel.updateQuery(query = it) },
            categories = state.categories,
            selectedCategoryId = state.selectedCategoryId,
            onCategoryClick = { searchViewModel.updateSelectedCategory(categoryId = it) },
            resultados = state.resultados,
            errorMessageRes = state.errorMessageRes,
            onAlbumClick = onAlbumClick,
            modifier = modifier,
        )
    }
}

@Composable
fun SearchScreenContent(
    query: String,
    onQueryChange: (String) -> Unit,
    categories: List<SearchCategoryUi>,
    selectedCategoryId: String?,
    onCategoryClick: (String) -> Unit,
    resultados: List<SongReviewUi>,
    errorMessageRes: Int?,
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

            item {
                ErrorMessage(
                    messageRes = errorMessageRes,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }

            // Sin busqueda se ven las categorias; tocar otra vez la elegida la quita
            if (query.isBlank()) {
                item {
                    BrowseByHeader(modifier = Modifier.padding(horizontal = 24.dp))
                }

                items(categories, key = { it.id }) { category ->
                    SearchCategoryRow(
                        category = category,
                        selected = category.id == selectedCategoryId,
                        onClick = { onCategoryClick(category.id) },
                        modifier = Modifier.padding(horizontal = 24.dp),
                    )
                }

                // Debajo de las categorias van todos los albumes, en el orden de la elegida
                item {
                    Text(
                        text = stringResource(R.string.todos_los_albumes),
                        modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 28.dp, bottom = 8.dp),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                    )
                }
            }

            if (resultados.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.sin_resultados),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 28.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 16.sp,
                    )
                }
            }

            items(resultados, key = { it.id }) { songReview ->
                ReviewAlbumRow(
                    cover = songReview.album.cover,
                    albumTitle = songReview.album.title,
                    artistName = songReview.album.artist,
                    rating = songReview.estrellas,
                    coverSize = 64.dp,
                    coverCorner = 10.dp,
                    starSize = 14.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAlbumClick(songReview.id) }
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                )
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
    MixtappTheme(darkTheme = true) {
        SearchScreenContent(
            query = "",
            onQueryChange = {},
            categories = LocalSearchCategoriesProvider.categories,
            selectedCategoryId = null,
            onCategoryClick = {},
            resultados = LocalSongReviewProvider.songs,
            errorMessageRes = null,
            onAlbumClick = {},
        )
    }
}
