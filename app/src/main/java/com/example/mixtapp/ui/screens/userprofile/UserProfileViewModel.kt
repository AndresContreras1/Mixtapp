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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UserProfileState())
    val uiState: StateFlow<UserProfileState> = _uiState

    fun getUserProfile(userId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
            val result = usuarioRepository.getUsuarioById(id = userId)
            val seguidoresResult = usuarioRepository.getSeguidoresDeUsuario(userId = userId)
            val seguidosResult = usuarioRepository.getSeguidosDeUsuario(userId = userId)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        usuario = result.getOrNull(),
                        seguidores = seguidoresResult.getOrNull() ?: emptyList(),
                        seguidos = seguidosResult.getOrNull() ?: emptyList(),
                        isLoading = false
                    )
                }
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

    fun abrirSeguidores() {
        _uiState.update { it.copy(tipoListaAbierta = TipoLista.SEGUIDORES) }
    }

    fun abrirSeguidos() {
        _uiState.update { it.copy(tipoListaAbierta = TipoLista.SEGUIDOS) }
    }

    fun cerrarLista() {
        _uiState.update { it.copy(tipoListaAbierta = null) }
    }

    fun seguirDejarDeSeguir() {
        val usuario = _uiState.value.usuario ?: return
        if (_uiState.value.cambiandoSeguimiento || usuario.esMiPerfil) return
        _uiState.update { it.copy(cambiandoSeguimiento = true, errorSeguimientoRes = null) }
        viewModelScope.launch {
            val result = usuarioRepository.actualizarSeguimiento(usuario.id, !usuario.siguiendo)
            val seguidoresResult = usuarioRepository.getSeguidoresDeUsuario(usuario.id)
            val seguidosResult = usuarioRepository.getSeguidosDeUsuario(usuario.id)
            _uiState.update {
                it.copy(
                    usuario = result.getOrNull() ?: it.usuario,
                    seguidores = seguidoresResult.getOrNull() ?: it.seguidores,
                    seguidos = seguidosResult.getOrNull() ?: it.seguidos,
                    cambiandoSeguimiento = false,
                    errorSeguimientoRes = if (result.isFailure) R.string.error_actualizar_seguimiento else null,
                )
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
