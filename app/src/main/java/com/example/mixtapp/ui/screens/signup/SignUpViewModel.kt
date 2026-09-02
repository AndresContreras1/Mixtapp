package com.example.mixtapp.ui.screens.signup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpState())
    val uiState: StateFlow<SignUpState> = _uiState.asStateFlow()

    fun updateUsuario(usuario: String) {
        _uiState.update { it.copy(usuario = usuario) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun updateContrasena(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena) }
        validarContrasenas()
    }

    fun updateConfirmarContrasena(confirmarContrasena: String) {
        _uiState.update { it.copy(confirmarContrasena = confirmarContrasena) }
        validarContrasenas()
    }

    fun mostrarEsconderContrasena() {
        val valorActual = _uiState.value.contrasenaVisible
        _uiState.update { it.copy(contrasenaVisible = !valorActual) }
    }

    fun mostrarEsconderConfirmar() {
        val valorActual = _uiState.value.confirmarVisible
        _uiState.update { it.copy(confirmarVisible = !valorActual) }
    }

    fun updateTerminos(terminos: Boolean) {
        _uiState.update { it.copy(terminos = terminos) }
    }

    // El error solo se muestra cuando ya se escribio algo en la confirmacion
    private fun validarContrasenas() {
        val estado = _uiState.value
        val hayError = estado.confirmarContrasena.isNotEmpty() &&
                estado.contrasena != estado.confirmarContrasena

        _uiState.update { it.copy(mostrarErrorContrasenas = hayError) }
    }
}
