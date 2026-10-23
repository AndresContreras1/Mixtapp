package com.example.mixtapp.ui.screens.followlist

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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowListState())
    val uiState: StateFlow<FollowListState> = _uiState.asStateFlow()

    // Buscar la lista es responsabilidad del ViewModel, no de la navegacion
    fun getUsuarios(userId: String, tipo: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val result = if (tipo == LISTA_SEGUIDORES) {
                usuarioRepository.getSeguidores(userId = userId)
            } else {
                usuarioRepository.getSeguidos(userId = userId)
            }

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(usuarios = result.getOrNull() ?: emptyList(), isLoading = false, errorMessageRes = null)
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = mensajeDeError(result.exceptionOrNull()))
                }
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
