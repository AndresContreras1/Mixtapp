package com.example.mixtapp.ui.screens.userprofile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private var profileSubscription: kotlinx.coroutines.Job? = null

    private var reviewsSubscription: kotlinx.coroutines.Job? = null

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState: StateFlow<UserProfileState> = _uiState.asStateFlow()

    // El perfil llega con sus contadores y con si ya lo sigo
    fun getUserProfile(userId: String) {
        profileSubscription?.cancel()
        profileSubscription = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            usuarioRepository.observeUsuario(userId).collect { result ->

                if (result.isSuccess) {
                    _uiState.update { it.copy(usuario = result.getOrNull(), isLoading = false) }
                } else {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessageRes = mensajeDeError(result.exceptionOrNull()))
                    }
                }
            }
        }
    }

    fun getUserReviews(userId: String) {
        reviewsSubscription?.cancel()
        reviewsSubscription = viewModelScope.launch {
            _uiState.update { it.copy(cargandoResenas = true, errorResenasRes = null) }
            usuarioRepository.observeReviews(userId).collect { result ->

                if (result.isSuccess) {
                    val reviews = (result.getOrNull() ?: emptyList()).sortedByDescending { it.date }
                    _uiState.update { it.copy(reviews = reviews, cargandoResenas = false) }
                } else {
                    _uiState.update {
                        it.copy(cargandoResenas = false, errorResenasRes = mensajeDeError(result.exceptionOrNull()))
                    }
                }
            }
        }
    }

    // El seguimiento confirmado llega por la suscripcion del perfil.
    fun followOrUnfollow() {
        val usuario = _uiState.value.usuario ?: return

        viewModelScope.launch {
            val result = usuarioRepository.followOrUnfollow(targetUserId = usuario.id)

            if (result.isSuccess) {
                // El listener aplica el estado confirmado sin duplicar contadores.
            } else {
                val error = result.exceptionOrNull()

                _uiState.update {
                    it.copy(
                        errorSeguimientoRes = if (error is SinConexionException) {
                            R.string.error_sin_conexion
                        } else {
                            R.string.error_actualizar_seguimiento
                        }
                    )
                }
            }
        }
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.perfil_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> R.string.error_cargar_contenido
    }
}
