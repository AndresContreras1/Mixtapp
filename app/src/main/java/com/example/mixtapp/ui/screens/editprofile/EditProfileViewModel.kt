package com.example.mixtapp.ui.screens.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.MAX_BIO_LENGTH
import com.example.mixtapp.data.model.MAX_NOMBRE_LENGTH
import com.example.mixtapp.data.model.MAX_USERNAME_LENGTH
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
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
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usuarioRepository: UsuarioRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState: StateFlow<EditProfileState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos actuales se cargan al crear el ViewModel
    init {
        getMiPerfil()
    }

    private fun getMiPerfil() {
        val usuarioDelCorreo = authRepository.usuarioDelCorreo

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorCargaRes = null) }

            val result = usuarioRepository.getMiPerfil()

            if (result.isSuccess) {
                val perfil = result.getOrNull()

                _uiState.update {
                    it.copy(
                        nombre = perfil?.nombre ?: "",
                        username = perfil?.username ?: usuarioDelCorreo,
                        bio = perfil?.bio ?: "",
                        isLoading = false,
                    )
                }
            } else if (result.exceptionOrNull() is ContenidoNoEncontradoException) {
                // Una cuenta sin datos en Firestore empieza con la parte del correo como usuario
                _uiState.update { it.copy(username = usuarioDelCorreo, sinDocumento = true, isLoading = false) }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorCargaRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_cargar_contenido))
                }
            }
        }
    }

    // Cada campo se corta en el limite del perfil (el mismo del registro) y escribir borra el
    // error anterior
    fun updateNombre(nombre: String) {
        _uiState.update { it.copy(nombre = nombre.take(MAX_NOMBRE_LENGTH), errorMessageRes = null) }
    }

    // Igual que el nombre, con su propio limite
    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username.take(MAX_USERNAME_LENGTH), errorMessageRes = null) }
    }

    // Igual que el nombre; el contador del area de texto muestra cuanto queda
    fun updateBio(bio: String) {
        _uiState.update { it.copy(bio = bio.take(MAX_BIO_LENGTH), errorMessageRes = null) }
    }

    // Decidir si se puede guardar es responsabilidad del ViewModel, no de la pantalla
    fun guardarCambios() {
        val estado = _uiState.value
        val nombre = estado.nombre.trim()
        val username = estado.username.trim()

        if (estado.guardando) return

        val errorRes = when {
            nombre.isBlank() || username.isBlank() -> R.string.error_campos_obligatorios
            username.contains(" ") -> R.string.error_username_espacios
            else -> null
        }

        // Si el formulario no pasa las validaciones no se consulta a Firestore
        if (errorRes != null) {
            _uiState.update { it.copy(errorMessageRes = errorRes) }
            return
        }

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(guardando = true, errorMessageRes = null) }

            val result = if (estado.sinDocumento) {
                usuarioRepository.registrarUsuario(username = username, nombre = nombre, bio = estado.bio.trim())
            } else {
                usuarioRepository.actualizarPerfil(nombre = nombre, username = username, bio = estado.bio.trim())
            }

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(guardando = false, guardado = true, sinDocumento = false, errorMessageRes = null)
                }
            } else {
                _uiState.update {
                    it.copy(guardando = false, errorMessageRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_guardar_perfil))
                }
            }
        }
    }
}
