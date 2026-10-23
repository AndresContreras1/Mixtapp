package com.example.mixtapp.ui.screens.followlist

import android.util.Log
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
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowListState())
    val uiState: StateFlow<FollowListState> = _uiState.asStateFlow()

    // El flujo se escucha mientras exista el ViewModel, una sola vez
    private var escuchandoLista = false

    // Datos en vivo: si alguien sigue o deja de seguir desde otro celular, la lista cambia sola
    fun getUsuarios(userId: String, tipo: String) {
        if (escuchandoLista) return
        escuchandoLista = true

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val usuarios = if (tipo == LISTA_SEGUIDORES) {
                usuarioRepository.getSeguidoresLive(userId = userId)
            } else {
                usuarioRepository.getSeguidosLive(userId = userId)
            }

            usuarios
                .catch { e ->
                    Log.d("FollowListViewModel", "getUsuarios: ${e.message}")
                    escuchandoLista = false
                    _uiState.update { it.copy(isLoading = false, errorMessageRes = mensajeDeError(e)) }
                }
                .collect { lista ->
                    _uiState.update { it.copy(usuarios = lista, isLoading = false, errorMessageRes = null) }
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
