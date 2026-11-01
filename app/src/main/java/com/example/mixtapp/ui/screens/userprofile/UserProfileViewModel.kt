package com.example.mixtapp.ui.screens.userprofile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.SinConexionException
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
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState: StateFlow<UserProfileState> = _uiState.asStateFlow()

    // Los flujos se escuchan mientras exista el ViewModel: si se volvieran a pedir cada vez que la
    // pantalla se muestra, se acumularian listeners
    private var escuchandoPerfil = false
    private var escuchandoResenas = false

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

    // Sus resenas en vivo: un like desde otro celular cambia el contador sin salir
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
                    val reviews = resenas.sortedByDescending { it.date }
                    _uiState.update { it.copy(reviews = reviews, cargandoResenas = false, errorResenasRes = null) }
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

}
