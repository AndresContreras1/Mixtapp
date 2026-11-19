package com.example.mixtapp.ui.screens.signup

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.CampoDeLaCuenta
import com.example.mixtapp.data.model.MAX_NOMBRE_LENGTH
import com.example.mixtapp.data.model.MAX_USERNAME_LENGTH
import com.example.mixtapp.data.model.MIN_PASSWORD_LENGTH
import com.example.mixtapp.data.model.esCorreoValido
import com.example.mixtapp.data.model.esUsuarioValido
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
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usuarioRepository: UsuarioRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpState())
    val uiState: StateFlow<SignUpState> = _uiState.asStateFlow()

    // El nombre se corta en el limite del perfil (el mismo de Editar perfil); escribir borra el
    // error anterior
    fun updateNombre(nombre: String) {
        _uiState.update { it.copy(nombre = nombre.take(MAX_NOMBRE_LENGTH), errorMessageRes = null, campoConError = null) }
    }

    // Igual que el nombre, con su propio maximo
    fun updateUsuario(usuario: String) {
        _uiState.update { it.copy(usuario = usuario.take(MAX_USERNAME_LENGTH), errorMessageRes = null, campoConError = null) }
    }

    // Al corregir el campo se limpia el error del intento anterior
    fun updateEmail(email: String) {
        _uiState.update { it.copy(email = email, errorMessageRes = null, campoConError = null) }
    }

    // Al corregir el campo se limpia el error del intento anterior
    fun updateContrasena(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorMessageRes = null, campoConError = null) }
    }

    // Al corregir el campo se limpia el error del intento anterior
    fun updateConfirmarContrasena(confirmarContrasena: String) {
        _uiState.update {
            it.copy(confirmarContrasena = confirmarContrasena, errorMessageRes = null, campoConError = null)
        }
    }

    // El ojo del campo de la contrasena: alterna entre verla y ocultarla con puntos
    fun mostrarEsconderContrasena() {
        val valorActual = _uiState.value.contrasenaVisible
        _uiState.update { it.copy(contrasenaVisible = !valorActual) }
    }

    // El ojo del campo de confirmar, independiente del de la contrasena
    fun mostrarEsconderConfirmar() {
        val valorActual = _uiState.value.confirmarVisible
        _uiState.update { it.copy(confirmarVisible = !valorActual) }
    }

    // Alternar la casilla es logica del ViewModel; el componente solo avisa del clic
    fun alternarTerminos() {
        val valorActual = _uiState.value.terminos
        _uiState.update { it.copy(terminos = !valorActual, errorMessageRes = null, campoConError = null) }
    }

    // Decidir si se puede crear la cuenta es responsabilidad del ViewModel, no de la navegacion
    fun signUpButtonPressed() {
        val estado = _uiState.value

        if (estado.cargando) return

        // El mensaje y el campo que lo tiene: el primero vacio o el primero que no cumple su regla
        val (errorRes, campo) = when {
            estado.nombre.isBlank() -> R.string.error_campos_obligatorios to CampoDeLaCuenta.Nombre
            estado.usuario.isBlank() -> R.string.error_campos_obligatorios to CampoDeLaCuenta.Usuario
            estado.email.isBlank() -> R.string.error_campos_obligatorios to CampoDeLaCuenta.Correo
            estado.contrasena.isBlank() -> R.string.error_campos_obligatorios to CampoDeLaCuenta.Contrasena
            estado.confirmarContrasena.isBlank() -> R.string.error_campos_obligatorios to CampoDeLaCuenta.Confirmar

            !esUsuarioValido(estado.usuario.trim()) ->
                R.string.error_username_espacios to CampoDeLaCuenta.Usuario

            !esCorreoValido(estado.email.trim()) ->
                R.string.error_email_invalido to CampoDeLaCuenta.Correo

            estado.contrasena.length < MIN_PASSWORD_LENGTH ->
                R.string.error_contrasena_corta to CampoDeLaCuenta.Contrasena

            estado.contrasena != estado.confirmarContrasena ->
                R.string.error_contrasenas_no_coinciden to CampoDeLaCuenta.Confirmar

            !estado.terminos ->
                R.string.error_terminos to CampoDeLaCuenta.Terminos

            else -> null to null
        }

        // Si el formulario no pasa las validaciones locales no se consulta a Firebase
        if (errorRes != null) {
            _uiState.update { it.copy(errorMessageRes = errorRes, campoConError = campo, navigate = false) }
            return
        }

        signUp(
            nombre = estado.nombre.trim(),
            usuario = estado.usuario.trim(),
            // El correo va sin los espacios que deja el teclado al autocompletar
            email = estado.email.trim(),
            contrasena = estado.contrasena,
        )
    }

    // signUp es suspend, asi que se lanza la corrutina donde se necesita
    private fun signUp(nombre: String, usuario: String, email: String, contrasena: String) {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(cargando = true, errorMessageRes = null, campoConError = null) }

            // Primero se crea la cuenta en Authentication y con su uid se guardan los datos en Firestore
            val result = authRepository.signUp(email = email, password = contrasena)

            if (result.isSuccess) {
                val registro = usuarioRepository.registrarUsuario(username = usuario, nombre = nombre, bio = "")

                if (registro.isSuccess) {
                    _uiState.update {
                        it.copy(cargando = false, errorMessageRes = null, navigate = true)
                    }
                } else {
                    // La cuenta ya quedo en Authentication, pero sin sus datos: se cierra la sesion y
                    // se pide iniciar sesion, que crea el documento con el usuario del correo
                    Log.d("SignUpViewModel", registro.exceptionOrNull()?.message.toString())
                    authRepository.signOut()

                    _uiState.update {
                        it.copy(cargando = false, errorMessageRes = R.string.error_registro_datos, navigate = false)
                    }
                }
            } else {
                Log.d("SignUpViewModel", result.exceptionOrNull()?.message.toString())

                _uiState.update {
                    it.copy(
                        cargando = false,
                        errorMessageRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_registro),
                        navigate = false,
                    )
                }
            }
        }
    }
}
