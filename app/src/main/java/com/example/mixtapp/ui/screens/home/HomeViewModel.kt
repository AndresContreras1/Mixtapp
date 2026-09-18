package com.example.mixtapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.SocialRepository
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
            val tendencia = albumRepository.getTrendingSongReview()
            val actividad = socialRepository.getFriendActivity()
            val filtroInicial = homeFilters.first().id
            val albums = aplicarFiltro(filtroId = filtroInicial)

            if (tendencia.isSuccess && actividad.isSuccess && albums != null) {
                _uiState.update {
                    it.copy(
                        albums = albums,
                        trending = tendencia.getOrNull(),
                        friendActivity = actividad.getOrNull(),
                        filters = homeFilters,
                        selectedFilterId = filtroInicial,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update { it.copy(errorMessageRes = R.string.error_cargar_contenido) }
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

    private suspend fun aplicarFiltro(filtroId: String): List<SongReviewUi>? = when (filtroId) {
        FILTRO_TENDENCIAS -> {
            val result = albumRepository.getSongReviews()
            result.getOrNull()?.sortedByDescending { it.rating }?.take(3)
        }

        FILTRO_AMIGOS -> albumesDeAmigos()

        else -> albumRepository.getPopularSongReviews().getOrNull()
    }

    private suspend fun albumesDeAmigos(): List<SongReviewUi>? {
        val following = socialRepository.getFollowing().getOrNull() ?: return null
        val todos = albumRepository.getSongReviews().getOrNull() ?: return null
        val idsDeAmigos = following.reviews.map { it.album.id }

        return todos.filter { it.album.id in idsDeAmigos }
    }
}
