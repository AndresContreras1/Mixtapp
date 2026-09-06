package com.example.mixtapp.ui.screens.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.ui.screens.login.MinPasswordLength
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpState())
    val uiState: StateFlow<SignUpState> = _uiState.asStateFlow()

    fun updateUsuario(usuario: String) {
        _uiState.update { it.copy(usuario = usuario, errorMessageRes = null) }
    }

    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessageRes = null) }
    }

    fun updateContrasena(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorMessageRes = null) }
        validarContrasenas()
    }

    fun updateConfirmarContrasena(confirmarContrasena: String) {
        _uiState.update {
            it.copy(confirmarContrasena = confirmarContrasena, errorMessageRes = null)
        }
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

    // Alternar la casilla es logica del ViewModel; el componente solo avisa del clic
    fun alternarTerminos() {
        val valorActual = _uiState.value.terminos
        _uiState.update { it.copy(terminos = !valorActual, errorMessageRes = null) }
    }

    // El error solo se muestra cuando ya se escribio algo en la confirmacion
    private fun validarContrasenas() {
        val estado = _uiState.value
        val hayError = estado.confirmarContrasena.isNotEmpty() &&
                estado.contrasena != estado.confirmarContrasena

        _uiState.update { it.copy(mostrarErrorContrasenas = hayError) }
    }

    // Decidir si se puede crear la cuenta es responsabilidad del ViewModel, no de la navegacion
    fun signUpButtonPressed() {
        val estado = _uiState.value

        val errorRes = when {
            estado.usuario.isBlank() ||
                    estado.email.isBlank() ||
                    estado.contrasena.isBlank() ||
                    estado.confirmarContrasena.isBlank() ->
                R.string.error_campos_obligatorios

            !estado.email.contains("@") ->
                R.string.error_email_invalido

            estado.contrasena.length < MinPasswordLength ->
                R.string.password_corta

            estado.contrasena != estado.confirmarContrasena ->
                R.string.passwords_no_coinciden

            !estado.terminos ->
                R.string.error_terminos

            else -> null
        }

        // Si el formulario no pasa las validaciones locales no se consulta a Firebase
        if (errorRes != null) {
            _uiState.update { it.copy(errorMessageRes = errorRes, navigate = false) }
            return
        }

        signUp(email = estado.email, contrasena = estado.contrasena)
    }

    // signUp es suspend, asi que se lanza la corrutina donde se necesita
    private fun signUp(email: String, contrasena: String) {
        viewModelScope.launch {
            try {
                authRepository.signUp(email = email, password = contrasena)
                // Autorizar la navegacion va dentro del try: si Firebase falla, no se entra
                _uiState.update { it.copy(errorMessageRes = null, navigate = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(errorMessageRes = R.string.error_registro, navigate = false)
                }
            }
        }
    }
}
