package com.example.mixtapp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.ui.screens.search.model.CATEGORIA_MAS_POPULARES
import com.example.mixtapp.ui.screens.search.model.CATEGORIA_MEJOR_CALIFICADOS
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
        viewModelScope.launch {
            val todos = albumRepository.getSongReviews().getOrNull() ?: emptyList()

            _uiState.update {
                it.copy(
                    query = query,
                    selectedCategoryId = if (query.isBlank()) it.selectedCategoryId else null,
                    resultados = buscarPorNombre(query = query, todos = todos),
                )
            }
        }
    }

    fun updateSelectedCategory(categoryId: String) {
        val yaEstaba = _uiState.value.selectedCategoryId == categoryId

        viewModelScope.launch {
            val todos = albumRepository.getSongReviews().getOrNull() ?: emptyList()

            _uiState.update {
                it.copy(
                    selectedCategoryId = if (yaEstaba) null else categoryId,
                    resultados = if (yaEstaba) {
                        emptyList()
                    } else {
                        ordenarPorCategoria(categoryId = categoryId, todos = todos)
                    },
                )
            }
        }
    }

    private fun buscarPorNombre(query: String, todos: List<SongReviewUi>): List<SongReviewUi> {
        if (query.isBlank()) return emptyList()

        return todos.filter { songReview ->
            songReview.album.title.contains(query, ignoreCase = true) ||
                    songReview.album.artist.contains(query, ignoreCase = true)
        }
    }

    // Ordenar por categoria es logica de negocio, no de la pantalla
    private fun ordenarPorCategoria(
        categoryId: String,
        todos: List<SongReviewUi>,
    ): List<SongReviewUi> = when (categoryId) {
        CATEGORIA_MAS_POPULARES -> todos.sortedByDescending { numeroDeCalificaciones(it.ratingCount) }
        CATEGORIA_MEJOR_CALIFICADOS -> todos.sortedByDescending { it.rating }
        else -> todos.sortedByDescending { it.album.year }
    }

    // "41.2k" no se puede ordenar como texto: 9.8k quedaria antes que 41.2k
    private fun numeroDeCalificaciones(ratingCount: String): Double {
        val sinSufijo = ratingCount.removeSuffix("k").toDoubleOrNull() ?: return 0.0

        return if (ratingCount.endsWith("k")) sinSufijo * 1000 else sinSufijo
    }
}
