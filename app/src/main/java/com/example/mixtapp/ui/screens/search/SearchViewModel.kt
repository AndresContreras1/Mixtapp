package com.example.mixtapp.ui.screens.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getCategories()
    }

    private fun getCategories() {
        _uiState.update { it.copy(categories = LocalSearchCategoriesProvider.categories) }
    }

    fun updateQuery(query: String) {
        _uiState.update {
            it.copy(
                query = query,
                resultados = buscarAlbumes(query = query),
            )
        }
    }

    private fun buscarAlbumes(query: String): List<SongReviewUi> {
        if (query.isBlank()) return emptyList()

        return LocalSongReviewProvider.songs.filter { album ->
            album.title.contains(query, ignoreCase = true) ||
                    album.artist.contains(query, ignoreCase = true)
        }
    }

    fun updateSelectedCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }
}
