package com.example.mixtapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.data.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            val result = albumRepository.getSearchCategories()

            if (result.isSuccess) {
                _uiState.update { it.copy(categories = result.getOrNull() ?: emptyList()) }
            }
        }
    }

    fun updateQuery(query: String) {
        _uiState.update { it.copy(query = query) }

        viewModelScope.launch {
            val result = albumRepository.getSongReviews()

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(resultados = buscarAlbumes(query = query, todos = result.getOrNull() ?: emptyList()))
                }
            }
        }
    }

    private fun buscarAlbumes(query: String, todos: List<SongReviewUi>): List<SongReviewUi> {
        if (query.isBlank()) return emptyList()

        return todos.filter { songReview ->
            songReview.album.title.contains(query, ignoreCase = true) ||
                    songReview.album.artist.contains(query, ignoreCase = true)
        }
    }

    fun updateSelectedCategory(categoryId: String) {
        _uiState.update { it.copy(selectedCategoryId = categoryId) }
    }
}
