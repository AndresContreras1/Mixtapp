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
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.ui.screens.home.model.FILTRO_AMIGOS
import com.example.mixtapp.ui.screens.home.model.FILTRO_TENDENCIAS
import com.example.mixtapp.ui.screens.home.model.homeFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val albumRepository: AlbumRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getAlbums()
    }

    private fun getAlbums() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            val filtroInicial = homeFilters.first().id

            // Los albumes y mis resenas salen a la vez y se espera la mas lenta
            val albumesPendientes = async { albumRepository.getSongReviews() }
            val misResenasPendientes = async { reviewRepository.getMyReviews() }

            val resultAlbumes = albumesPendientes.await()
            val resultMisResenas = misResenasPendientes.await()

            // Aunque mis resenas no lleguen, el inicio muestra los albumes con el error arriba
            if (resultAlbumes.isSuccess) {
                val todos = resultAlbumes.getOrNull() ?: emptyList()
                val idsDeMisResenas = (resultMisResenas.getOrNull() ?: emptyList()).map { it.id }

                _uiState.update {
                    it.copy(
                        albums = aplicarFiltro(
                            filtroId = filtroInicial,
                            todos = todos,
                            idsDeMisResenas = idsDeMisResenas,
                        ),
                        todos = todos,
                        trending = enTendencia(todos = todos),
                        friendActivity = actividadDeAmigos(
                            todos = todos,
                            idsDeMisResenas = idsDeMisResenas,
                        ),
                        filters = homeFilters,
                        selectedFilterId = filtroInicial,
                        idsDeMisResenas = idsDeMisResenas,
                        isLoading = false,
                        errorMessageRes = if (resultMisResenas.isSuccess) {
                            null
                        } else {
                            mensajeDeError(
                                error = resultMisResenas.exceptionOrNull(),
                                generico = R.string.error_cargar_contenido,
                            )
                        },
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = mensajeDeError(
                            error = resultAlbumes.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
                    )
                }
            }
        }
    }

    fun refrescarFotoDePerfil() {
        val foto = authRepository.currentUser?.photoUrl?.toString() ?: ""

        _uiState.update { it.copy(profileImageUrl = foto) }
    }

    // Los albumes ya estan cargados: cambiar de filtro no vuelve a pedirlos al backend
    fun updateSelectedFilter(filtroId: String) {
        val estado = _uiState.value

        _uiState.update {
            it.copy(
                selectedFilterId = filtroId,
                albums = aplicarFiltro(
                    filtroId = filtroId,
                    todos = estado.todos,
                    idsDeMisResenas = estado.idsDeMisResenas,
                ),
            )
        }
    }

    private fun aplicarFiltro(
        filtroId: String,
        todos: List<SongReviewUi>,
        idsDeMisResenas: List<String>,
    ): List<SongReviewUi> = when (filtroId) {
        FILTRO_TENDENCIAS -> todos.sortedByDescending { it.reviews.size }

        // Los albumes que reseno alguien distinto a mi
        FILTRO_AMIGOS -> todos.filter { songReview ->
            songReview.reviews.any { it.id !in idsDeMisResenas }
        }

        else -> todos
    }

    // El album con mas resenas
    private fun enTendencia(todos: List<SongReviewUi>): SongReviewUi? =
        todos.sortedByDescending { it.reviews.size }.firstOrNull()

    // La resena mas reciente que escribio otro usuario
    private fun actividadDeAmigos(
        todos: List<SongReviewUi>,
        idsDeMisResenas: List<String>,
    ): FriendActivityUi? {
        val actividades = mutableListOf<FriendActivityUi>()

        todos.forEach { songReview ->
            songReview.reviews.forEach { review ->
                if (review.id !in idsDeMisResenas) {
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
