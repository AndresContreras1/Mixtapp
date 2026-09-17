package com.example.mixtapp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.SocialRepository
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
            val populares = albumRepository.getPopularSongReviews()
            val tendencia = albumRepository.getTrendingSongReview()
            val actividad = socialRepository.getFriendActivity()

            if (populares.isSuccess && tendencia.isSuccess && actividad.isSuccess) {
                _uiState.update {
                    it.copy(
                        albums = populares.getOrNull() ?: emptyList(),
                        trending = tendencia.getOrNull(),
                        friendActivity = actividad.getOrNull(),
                        filters = homeFilters,
                        selectedFilterId = homeFilters.first().id,
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
        _uiState.update { it.copy(selectedFilterId = filtroId) }
    }
}
