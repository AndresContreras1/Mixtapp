package com.example.mixtapp.ui.screens.signup

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MIN_PASSWORD_LENGTH
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.CorreoYaRegistradoException
import com.example.mixtapp.data.repository.CredencialesInvalidasException
import com.example.mixtapp.data.repository.DemasiadosIntentosException
import com.example.mixtapp.data.repository.ErrorDeRegistroException
import com.example.mixtapp.data.repository.SinConexionException
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

        if (estado.cargando) return

        val errorRes = when {
            estado.usuario.isBlank() ||
                    estado.email.isBlank() ||
                    estado.contrasena.isBlank() ||
                    estado.confirmarContrasena.isBlank() ->
                R.string.error_campos_obligatorios

            !estado.email.contains("@") ->
                R.string.error_email_invalido

            estado.contrasena.length < MIN_PASSWORD_LENGTH ->
                R.string.error_contrasena_corta

            estado.contrasena.none { it.isUpperCase() } ||
                    estado.contrasena.none { it.isDigit() } ->
                R.string.error_contrasena_debil

            estado.contrasena != estado.confirmarContrasena ->
                R.string.error_contrasenas_no_coinciden

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
            _uiState.update { it.copy(cargando = true, errorMessageRes = null) }

            val result = authRepository.signUp(email = email, password = contrasena)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(cargando = false, errorMessageRes = null, navigate = true)
                }
            } else {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        errorMessageRes = mensajeDeError(result.exceptionOrNull()),
                        navigate = false,
                    )
                }
            }
        }
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?): Int = when (error) {
        is CorreoYaRegistradoException -> R.string.error_correo_ya_registrado
        is CredencialesInvalidasException -> R.string.error_datos_registro_invalidos
        is SinConexionException -> R.string.error_sin_conexion
        is DemasiadosIntentosException -> R.string.error_demasiados_intentos
        is ErrorDeRegistroException -> R.string.error_registro
        else -> R.string.error_registro
    }
}
