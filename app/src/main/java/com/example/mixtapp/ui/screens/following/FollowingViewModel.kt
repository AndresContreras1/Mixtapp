package com.example.mixtapp.ui.screens.following

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.ui.screens.following.model.FILTRO_CALIFICACIONES
import com.example.mixtapp.ui.screens.following.model.FILTRO_LISTAS
import com.example.mixtapp.ui.screens.following.model.FILTRO_RESENAS
import com.example.mixtapp.ui.screens.following.model.followingFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowingState())
    val uiState: StateFlow<FollowingState> = _uiState.asStateFlow()

    // El flujo se escucha mientras exista el ViewModel: si se volviera a pedir cada vez que la
    // pantalla se muestra, se acumularian listeners
    private var escuchandoSeguidos = false

    // Datos en vivo: una resena nueva, un like o seguir a alguien llegan solos, sin salir y volver
    fun getFollowing() {
        if (escuchandoSeguidos) return
        escuchandoSeguidos = true

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            socialRepository.getFollowingLive()
                .catch { e ->
                    Log.d("FollowingViewModel", "getFollowing: ${e.message}")
                    escuchandoSeguidos = false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { llegada ->
                    _uiState.update {
                        // Compartir solo existe en esta pantalla: se conserva al llegar datos nuevos
                        val compartidas = it.following?.reviews.orEmpty().filter { r -> r.isShared }.map { r -> r.id }
                        val following = llegada.copy(
                            reviews = llegada.reviews.map { r -> r.copy(isShared = r.id in compartidas) }
                        )
                        // Al llegar datos nuevos se conserva el filtro que estaba elegido
                        val filtroId = it.selectedFilterId.ifEmpty { followingFilters.first().id }

                        it.copy(
                            following = following,
                            reviews = aplicarFiltros(
                                friendQuery = it.friendQuery,
                                filtroId = filtroId,
                                todas = following.reviews,
                            ),
                            filters = followingFilters,
                            selectedFilterId = filtroId,
                            isLoading = false,
                            errorMessageRes = null,
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

    // No se copia el estado a mano: la transaccion cambia likesCount, eso dispara el flujo y llega
    // la resena con el corazon y el contador al dia
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_me_gusta,
                        )
                    )
                }
            }
        }
    }

    // Compartir solo marca la tarjeta en esta pantalla
    fun compartirQuitar(reviewId: String) {
        actualizarResena(reviewId = reviewId) { review -> review.copy(isShared = !review.isShared) }
    }

    // Cambia la resena en la lista completa y en la filtrada que se ve
    private fun actualizarResena(reviewId: String, cambio: (FollowingReviewUi) -> FollowingReviewUi) {
        _uiState.update {
            it.copy(
                following = it.following?.copy(
                    reviews = it.following.reviews.map { review ->
                        if (review.id == reviewId) cambio(review) else review
                    }
                ),
                reviews = it.reviews.map { review ->
                    if (review.id == reviewId) cambio(review) else review
                },
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
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}
