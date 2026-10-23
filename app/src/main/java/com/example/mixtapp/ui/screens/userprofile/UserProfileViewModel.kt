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

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState: StateFlow<UserProfileState> = _uiState.asStateFlow()

    // El perfil llega con sus contadores y con si ya lo sigo
    fun getUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val result = usuarioRepository.getUsuarioById(id = userId)

            if (result.isSuccess) {
                _uiState.update { it.copy(usuario = result.getOrNull(), isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = mensajeDeError(result.exceptionOrNull()))
                }
            }
        }
    }

    fun getUserReviews(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoResenas = true, errorResenasRes = null) }
            val result = usuarioRepository.getReviewsDeUsuario(id = userId)

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

    // Si sale bien se invierte el boton y el contador de seguidores sube o baja, sin volver a
    // pedir el perfil
    fun followOrUnfollow() {
        val usuario = _uiState.value.usuario ?: return

        viewModelScope.launch {
            val result = usuarioRepository.followOrUnfollow(targetUserId = usuario.id)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        usuario = it.usuario?.copy(
                            seguidores = if (it.usuario.siguiendo) it.usuario.seguidores - 1 else it.usuario.seguidores + 1,
                            siguiendo = !it.usuario.siguiendo,
                        ),
                        errorSeguimientoRes = null,
                    )
                }
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
