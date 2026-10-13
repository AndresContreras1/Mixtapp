package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.ui.screens.home.model.FILTRO_AMIGOS
import com.example.mixtapp.ui.screens.home.model.FILTRO_TENDENCIAS
import com.example.mixtapp.ui.screens.home.model.homeFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val albumRepository: AlbumRepository
) : ViewModel() {

    // La foto de perfil se lee al crear el estado, como en clase
    private val _uiState = MutableStateFlow(
        HomeState(profileImageUrl = authRepository.currentUser?.photoUrl?.toString() ?: "")
    )
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getAlbums()
    }

    private fun getAlbums() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            val filtroInicial = homeFilters.first().id

            // Cada album llega con sus resenas, y las mias ya vienen marcadas
            val result = albumRepository.getSongReviews()

            if (result.isSuccess) {
                val todos = result.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        albums = aplicarFiltro(filtroId = filtroInicial, todos = todos),
                        todos = todos,
                        trending = enTendencia(todos = todos),
                        friendActivity = actividadDeAmigos(todos = todos),
                        filters = homeFilters,
                        selectedFilterId = filtroInicial,
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

    // Los albumes ya estan cargados: cambiar de filtro no vuelve a pedirlos al backend
    fun updateSelectedFilter(filtroId: String) {
        val todos = _uiState.value.todos

        _uiState.update {
            it.copy(
                selectedFilterId = filtroId,
                albums = aplicarFiltro(filtroId = filtroId, todos = todos),
            )
        }
    }

    private fun aplicarFiltro(filtroId: String, todos: List<SongReviewUi>): List<SongReviewUi> =
        when (filtroId) {
            FILTRO_TENDENCIAS -> todos.sortedByDescending { it.reviews.size }

            // Los albumes que reseno alguien distinto a mi
            FILTRO_AMIGOS -> todos.filter { songReview ->
                songReview.reviews.any { !it.esMia }
            }

            else -> todos
        }

    // El album con mas resenas
    private fun enTendencia(todos: List<SongReviewUi>): SongReviewUi? =
        todos.sortedByDescending { it.reviews.size }.firstOrNull()

    // De las resenas de otros usuarios, la del album que se escucho mas recientemente
    private fun actividadDeAmigos(todos: List<SongReviewUi>): FriendActivityUi? {
        val actividades = mutableListOf<FriendActivityUi>()

        todos.forEach { songReview ->
            songReview.reviews.forEach { review ->
                if (!review.esMia) {
                    actividades.add(
                        FriendActivityUi(
                            id = review.id,
                            album = songReview.album,
                            rating = review.rating,
                            quote = review.content,
                            author = review.author,
                            date = review.fecha,
                        )
                    )
                }
            }
        }

        // La fecha va como AAAA-MM-DD, asi que ordenar el texto ordena las fechas
        return actividades.sortedByDescending { it.date }.firstOrNull()
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}
