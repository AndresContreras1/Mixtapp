package com.example.mixtapp.ui.screens.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.MIN_PASSWORD_LENGTH
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.utils.mensajeDeError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usuarioRepository: UsuarioRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
    val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    fun updateEmail(email: String) {
        // Al corregir el campo se limpia el error del intento anterior
        _uiState.update { it.copy(email = email, errorMessageRes = null) }
    }

    fun updateContrasena(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorMessageRes = null) }
    }

    fun mostrarEsconderContrasena() {
        val valorActual = _uiState.value.contrasenaVisible
        _uiState.update { it.copy(contrasenaVisible = !valorActual) }
    }

    // Decidir si se puede entrar es responsabilidad del ViewModel, no de la navegacion
    fun loginButtonPressed() {
        val estado = _uiState.value

        if (estado.cargando) return

        val errorRes = when {
            estado.email.isBlank() || estado.contrasena.isBlank() ->
                R.string.error_campos_obligatorios

            !estado.email.contains("@") ->
                R.string.error_email_invalido

            estado.contrasena.length < MIN_PASSWORD_LENGTH ->
                R.string.error_contrasena_corta

            else -> null
        }

        // Si el formulario no pasa las validaciones locales no se consulta a Firebase
        if (errorRes != null) {
            _uiState.update { it.copy(errorMessageRes = errorRes, navigate = false) }
            return
        }

        signIn(email = estado.email, contrasena = estado.contrasena)
    }

    // signIn es suspend, asi que se lanza la corrutina donde se necesita
    private fun signIn(email: String, contrasena: String) {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(cargando = true, errorMessageRes = null) }

            val result = authRepository.signIn(email = email, password = contrasena)

            if (result.isSuccess) {
                // El token de este celular se guarda para recibir las notificaciones. Si falla,
                // se entra igual: la sesion ya se inicio y solo se pierden las notificaciones
                val token = usuarioRepository.actualizarToken()

                if (!token.isSuccess) {
                    Log.d("LoginViewModel", token.exceptionOrNull()?.message.toString())
                }

                _uiState.update {
                    it.copy(cargando = false, errorMessageRes = null, navigate = true)
                }
            } else {
                Log.d("LoginViewModel", result.exceptionOrNull()?.message.toString())

                _uiState.update {
                    it.copy(
                        cargando = false,
                        errorMessageRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_inicio_sesion),
                        navigate = false,
                    )
                }
            }
        }
    }

}
