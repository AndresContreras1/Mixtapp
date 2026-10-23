package com.example.mixtapp.ui.screens.search

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.CATEGORIA_FECHA_LANZAMIENTO
import com.example.mixtapp.data.model.CATEGORIA_MAS_POPULARES
import com.example.mixtapp.data.model.CATEGORIA_MEJOR_CALIFICADOS
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.SinConexionException
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

    // Las categorias no cambian, asi que se cargan una vez al crear el ViewModel
    init {
        getCategories()
    }

    private fun getCategories() {
        viewModelScope.launch {
            val result = albumRepository.getSearchCategories()

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(categories = result.getOrNull() ?: emptyList(), errorMessageRes = null)
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
                    )
                }
            }
        }
    }

    // La pantalla la llama cada vez que se muestra; buscar y ordenar trabajan sobre esta lista
    fun getAlbumes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val result = albumRepository.getAlbumReviews()

            if (result.isSuccess) {
                val todos = result.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        todos = todos,
                        // Si ya se escribio algo mientras cargaba, se resuelve con lo que llego
                        resultados = resultadosPara(
                            query = it.query,
                            categoriaId = it.selectedCategoryId,
                            todos = todos,
                        ),
                        isLoading = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
                    )
                }
            }
        }
    }

    fun updateQuery(query: String) {
        val estado = _uiState.value
        val categoria = if (query.isBlank()) estado.selectedCategoryId else null

        _uiState.update {
            it.copy(
                query = query,
                selectedCategoryId = categoria,
                resultados = resultadosPara(query = query, categoriaId = categoria, todos = estado.todos),
            )
        }
    }

    fun updateSelectedCategory(categoryId: String) {
        val estado = _uiState.value
        val categoria = if (estado.selectedCategoryId == categoryId) null else categoryId

        _uiState.update {
            it.copy(
                selectedCategoryId = categoria,
                resultados = resultadosPara(query = estado.query, categoriaId = categoria, todos = estado.todos),
            )
        }
    }

    private fun resultadosPara(
        query: String,
        categoriaId: String?,
        todos: List<AlbumReviewsUi>,
    ): List<AlbumReviewsUi> = when {
        query.isNotBlank() -> buscarPorNombre(query = query, todos = todos)
        categoriaId != null -> ordenarPorCategoria(categoryId = categoriaId, todos = todos)
        // Sin busqueda ni categoria se muestran todos los albumes
        else -> todos
    }

    private fun buscarPorNombre(query: String, todos: List<AlbumReviewsUi>): List<AlbumReviewsUi> =
        todos.filter { albumReviews ->
            albumReviews.album.title.contains(query, ignoreCase = true) ||
                    albumReviews.album.artist.contains(query, ignoreCase = true)
        }

    private fun ordenarPorCategoria(
        categoryId: String,
        todos: List<AlbumReviewsUi>,
    ): List<AlbumReviewsUi> = when (categoryId) {
        CATEGORIA_FECHA_LANZAMIENTO -> todos.sortedByDescending { it.album.year }
        CATEGORIA_MEJOR_CALIFICADOS -> todos.sortedByDescending { it.rating }
        // Los mas populares son los que tienen mas resenas
        CATEGORIA_MAS_POPULARES -> todos.sortedByDescending { it.reviews.size }
        else -> todos
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}
