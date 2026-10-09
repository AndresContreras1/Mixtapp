package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.SocialRepository
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

// Cuantos albumes caben en la fila de populares del Figma
const val ALBUMES_EN_LA_FILA = 3

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val albumRepository: AlbumRepository,
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getAlbums()
    }

    private fun getAlbums() {
        viewModelScope.launch {
            val filtroInicial = homeFilters.first().id

            // Las tres peticiones salen a la vez y se espera la mas lenta
            val tendencia = async { albumRepository.getTrendingSongReview() }
            val actividad = async { socialRepository.getFriendActivity() }
            val albums = async { aplicarFiltro(filtroId = filtroInicial) }

            val resultTendencia = tendencia.await()
            val resultActividad = actividad.await()
            val albumesDelFiltro = albums.await()

            if (resultTendencia.isSuccess && resultActividad.isSuccess && albumesDelFiltro != null) {
                _uiState.update {
                    it.copy(
                        albums = albumesDelFiltro,
                        trending = resultTendencia.getOrNull(),
                        friendActivity = resultActividad.getOrNull(),
                        filters = homeFilters,
                        selectedFilterId = filtroInicial,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = resultTendencia.exceptionOrNull()
                                ?: resultActividad.exceptionOrNull(),
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

    fun updateSelectedFilter(filtroId: String) {
        viewModelScope.launch {
            val albums = aplicarFiltro(filtroId = filtroId)

            if (albums != null) {
                _uiState.update {
                    it.copy(selectedFilterId = filtroId, albums = albums, errorMessageRes = null)
                }
            } else {
                _uiState.update { it.copy(errorMessageRes = R.string.error_actualizar_lista) }
            }
        }
    }

    // Los tres caminos devuelven como maximo los albumes que caben en la fila
    private suspend fun aplicarFiltro(filtroId: String): List<SongReviewUi>? = when (filtroId) {
        FILTRO_TENDENCIAS -> {
            val result = albumRepository.getSongReviews()

            result.getOrNull()?.sortedByDescending { it.rating }?.take(ALBUMES_EN_LA_FILA)
        }

        FILTRO_AMIGOS -> albumesDeAmigos()

        else -> albumRepository.getSongReviews().getOrNull()?.take(ALBUMES_EN_LA_FILA)
    }

    private suspend fun albumesDeAmigos(): List<SongReviewUi>? {
        val following = socialRepository.getFollowing().getOrNull() ?: return null
        val todos = albumRepository.getSongReviews().getOrNull() ?: return null
        val idsDeAmigos = following.reviews.map { it.album.id }

        return todos.filter { it.album.id in idsDeAmigos }.take(ALBUMES_EN_LA_FILA)
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}
