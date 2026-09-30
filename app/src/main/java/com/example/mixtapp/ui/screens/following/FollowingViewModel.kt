package com.example.mixtapp.ui.screens.following

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.ui.screens.following.model.FILTRO_CALIFICACIONES
import com.example.mixtapp.ui.screens.following.model.FILTRO_LISTAS
import com.example.mixtapp.ui.screens.following.model.FILTRO_RESENAS
import com.example.mixtapp.ui.screens.following.model.followingFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowingState())
    val uiState: StateFlow<FollowingState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getFollowing()
    }

    private fun getFollowing() {
        viewModelScope.launch {
            val result = socialRepository.getFollowing()

            if (result.isSuccess) {
                val following = result.getOrNull() ?: return@launch
                val filtroInicial = followingFilters.first().id
                val query = _uiState.value.friendQuery

                _uiState.update {
                    it.copy(
                        following = following,
                        reviews = aplicarFiltros(
                            friendQuery = query,
                            filtroId = filtroInicial,
                            todas = following.reviews,
                        ),
                        filters = followingFilters,
                        selectedFilterId = filtroInicial,
                        errorMessageRes = null,
                    )
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

    fun updateFriendQuery(friendQuery: String) {
        val filtroId = _uiState.value.selectedFilterId
        val todas = _uiState.value.following?.reviews ?: emptyList()

        _uiState.update {
            it.copy(
                friendQuery = friendQuery,
                reviews = aplicarFiltros(
                    friendQuery = friendQuery,
                    filtroId = filtroId,
                    todas = todas,
                ),
            )
        }
    }

    fun updateSelectedFilter(filtroId: String) {
        val query = _uiState.value.friendQuery
        val todas = _uiState.value.following?.reviews ?: emptyList()

        _uiState.update {
            it.copy(
                selectedFilterId = filtroId,
                reviews = aplicarFiltros(
                    friendQuery = query,
                    filtroId = filtroId,
                    todas = todas,
                ),
            )
        }
    }

    fun updateSelectedStory(storyId: String) {
        _uiState.update { it.copy(selectedStoryId = storyId) }
    }

    fun darQuitarLike(reviewId: String) {
        viewModelScope.launch {
            val result = socialRepository.darQuitarLikeFollowingReview(reviewId = reviewId)

            actualizarDesde(result = result, generico = R.string.error_me_gusta)
        }
    }

    fun compartirQuitar(reviewId: String) {
        viewModelScope.launch {
            val result = socialRepository.compartirQuitarFollowingReview(reviewId = reviewId)

            actualizarDesde(result = result, generico = R.string.error_compartir)
        }
    }

    private fun actualizarDesde(result: Result<FollowingUi>, @StringRes generico: Int) {
        if (result.isFailure) {
            _uiState.update {
                it.copy(
                    errorMessageRes = mensajeDeError(
                        error = result.exceptionOrNull(),
                        generico = generico,
                    )
                )
            }
            return
        }

        val following = result.getOrNull() ?: return
        val query = _uiState.value.friendQuery
        val filtroId = _uiState.value.selectedFilterId

        _uiState.update {
            it.copy(
                following = following,
                reviews = aplicarFiltros(
                    friendQuery = query,
                    filtroId = filtroId,
                    todas = following.reviews,
                ),
                errorMessageRes = null,
            )
        }
    }

    private fun aplicarFiltros(
        friendQuery: String,
        filtroId: String,
        todas: List<FollowingReviewUi>,
    ): List<FollowingReviewUi> {
        val porAmigo = todas.filter { it.reviewerName.contains(friendQuery, ignoreCase = true) }

        return when (filtroId) {
            FILTRO_RESENAS -> porAmigo.filter { it.reviewText.isNotBlank() }
            FILTRO_CALIFICACIONES -> porAmigo.filter { it.reviewText.isBlank() }
            FILTRO_LISTAS -> emptyList()
            else -> porAmigo
        }
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        else -> generico
    }
}
