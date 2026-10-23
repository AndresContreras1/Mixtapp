package com.example.mixtapp.ui.screens.following

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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private var followingSubscription: kotlinx.coroutines.Job? = null

    private val _uiState = MutableStateFlow(FollowingState())
    val uiState: StateFlow<FollowingState> = _uiState.asStateFlow()

    // La pantalla la llama cada vez que se muestra: el init solo corre una vez al crear el
    // ViewModel, y al volver de un perfil o de una discusion se seguirian viendo los datos viejos
    fun getFollowing() {
        followingSubscription?.cancel()
        followingSubscription = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            socialRepository.observeFollowing().collect { result ->
                val sharedIds = _uiState.value.following?.reviews.orEmpty().filter { it.isShared }.map { it.id }.toSet()
                val following = result.getOrNull()?.let { value ->
                    value.copy(reviews = value.reviews.map { it.copy(isShared = it.id in sharedIds) })
                }

                if (result.isSuccess && following != null) {
                    // Al volver se conserva el filtro que estaba elegido
                    val filtroInicial = _uiState.value.selectedFilterId.ifEmpty { followingFilters.first().id }
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

    // Firestore confirma el like; la suscripcion actualiza el corazon y el contador.
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                // El listener aplica el estado confirmado sin duplicar contadores.
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
