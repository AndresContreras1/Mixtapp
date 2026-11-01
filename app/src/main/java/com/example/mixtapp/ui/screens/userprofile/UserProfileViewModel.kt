package com.example.mixtapp.ui.screens.userprofile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.utils.mensajeDeError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val reviewRepository: ReviewRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState: StateFlow<UserProfileState> = _uiState.asStateFlow()

    // Los flujos necesitan el id de la ruta, asi que los pide la pantalla. Se escuchan mientras
    // exista el ViewModel: si se volvieran a pedir cada vez que la pantalla se muestra, se
    // acumularian listeners
    private var escuchandoPerfil = false
    private var escuchandoResenas = false
    private var escuchandoFavoritos = false

    // El perfil llega en vivo con sus contadores y con si ya lo sigo
    fun getUserProfile(userId: String) {
        if (escuchandoPerfil) return
        escuchandoPerfil = true

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            usuarioRepository.getUsuarioByIdLive(id = userId)
                .catch { e ->
                    Log.d("UserProfileViewModel", "getUserProfile: ${e.message}")
                    escuchandoPerfil = false
                    _uiState.update { it.copy(isLoading = false, errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido, noEncontrado = R.string.perfil_no_encontrado)) }
                }
                .collect { usuario ->
                    _uiState.update { it.copy(usuario = usuario, isLoading = false, errorMessageRes = null) }
                }
        }
    }

    // Sus resenas en vivo, la ultima publicada o editada primero: un like desde otro celular cambia
    // el contador sin salir
    fun getUserReviews(userId: String) {
        if (escuchandoResenas) return
        escuchandoResenas = true

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(cargandoResenas = true, errorResenasRes = null) }

            usuarioRepository.getReviewsDeUsuarioLive(id = userId)
                .catch { e ->
                    Log.d("UserProfileViewModel", "getUserReviews: ${e.message}")
                    escuchandoResenas = false
                    _uiState.update { it.copy(cargandoResenas = false, errorResenasRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido, noEncontrado = R.string.perfil_no_encontrado)) }
                }
                .collect { resenas ->
                    _uiState.update { it.copy(reviews = resenas, cargandoResenas = false, errorResenasRes = null) }
                }
        }
    }

    // Sus favoritos son sus likes, el mas nuevo primero: si le da like a algo, aparece sin salir
    fun getUserFavoritos(userId: String) {
        if (escuchandoFavoritos) return
        escuchandoFavoritos = true

        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getFavoritosLive(userId = userId)
                .catch { e ->
                    Log.d("UserProfileViewModel", "getUserFavoritos: ${e.message}")
                    escuchandoFavoritos = false
                    _uiState.update { it.copy(errorFavoritosRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido)) }
                }
                .collect { favoritos ->
                    _uiState.update { it.copy(favoritos = favoritos, errorFavoritosRes = null) }
                }
        }
    }

    // No se copia el estado a mano: la transaccion cambia followersCount, eso dispara el flujo del
    // perfil y llegan el boton y el contador al dia
    fun followOrUnfollow() {
        val usuario = _uiState.value.usuario ?: return

        viewModelScope.launch(ioDispatcher) {
            val result = usuarioRepository.followOrUnfollow(targetUserId = usuario.id)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorSeguimientoRes = null) }
            } else {
                _uiState.update {
                    it.copy(errorSeguimientoRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_actualizar_seguimiento))
                }
            }
        }
    }

    // El corazon y el contador llegan con la resena nueva por el listener de sus resenas
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorLikeRes = null) }
            } else {
                _uiState.update {
                    it.copy(errorLikeRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_actualizar_like))
                }
            }
        }
    }
}
