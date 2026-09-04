package com.example.mixtapp.ui.screens.login

import androidx.lifecycle.ViewModel
import com.example.mixtapp.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Longitud minima de la contrasena. La comparten los dos formularios de la app
const val MinPasswordLength = 6

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        // Al corregir el campo se limpia el error del intento anterior
        _uiState.update { it.copy(email = email, errorMessageRes = null) }
    }

    fun updateContrasena(contrasena: String) {
        _uiState.update {
            it.copy(
                contrasena = contrasena,
                // El aviso solo aparece cuando ya se escribio algo y se queda corto
                mostrarErrorContrasena = contrasena.isNotEmpty() &&
                        contrasena.length < MinPasswordLength,
                errorMessageRes = null,
            )
        }
    }

    fun mostrarEsconderContrasena() {
        val valorActual = _uiState.value.passwordVisible
        _uiState.update { it.copy(passwordVisible = !valorActual) }
    }

    // Decidir si se puede entrar es responsabilidad del ViewModel, no de la navegacion
    fun loginButtonPressed() {
        val estado = _uiState.value

        val errorRes = when {
            estado.email.isBlank() || estado.contrasena.isBlank() ->
                R.string.error_campos_obligatorios

            !estado.email.contains("@") ->
                R.string.error_email_invalido

            estado.contrasena.length < MinPasswordLength ->
                R.string.password_corta

            else -> null
        }

        _uiState.update {
            it.copy(
                errorMessageRes = errorRes,
                navigate = errorRes == null,
            )
        }
    }
}
