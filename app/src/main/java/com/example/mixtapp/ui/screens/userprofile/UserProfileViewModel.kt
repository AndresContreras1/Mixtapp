package com.example.mixtapp.ui.screens.userprofile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
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
            val result = usuarioRepository.getReviewsDeUsuario(id = userId)

            if (result.isSuccess) {
                _uiState.update { it.copy(reviews = result.getOrNull() ?: emptyList()) }
            } else {
                _uiState.update { it.copy(errorMessageRes = mensajeDeError(result.exceptionOrNull())) }
            }
        }
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?): Int = when (error) {
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> R.string.error_cargar_contenido
    }
}
