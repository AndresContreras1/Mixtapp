package com.example.mixtapp.ui.screens.editprofile

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MAX_BIO_LENGTH
import com.example.mixtapp.data.model.MAX_NOMBRE_LENGTH
import com.example.mixtapp.data.model.MAX_USERNAME_LENGTH
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.SinSesionException
import com.example.mixtapp.data.repository.UsuarioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileState())
    val uiState: StateFlow<EditProfileState> = _uiState

    // No recibe parametros, asi que los datos actuales se cargan al crear el ViewModel
    init {
        getMiPerfil()
    }

    private fun getMiPerfil() {
        val usuarioDelCorreo = authRepository.currentUser?.email?.substringBefore("@") ?: ""

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

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
            } else {
                // Una cuenta sin datos en Firestore empieza con la parte del correo como usuario
                _uiState.update { it.copy(username = usuarioDelCorreo, isLoading = false) }
            }
        }
    }

    fun updateNombre(nombre: String) {
        _uiState.update { it.copy(nombre = nombre.take(MAX_NOMBRE_LENGTH), errorMessageRes = null) }
    }

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username.take(MAX_USERNAME_LENGTH), errorMessageRes = null) }
    }

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

        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, errorMessageRes = null) }

            val result = usuarioRepository.actualizarPerfil(
                nombre = nombre,
                username = username,
                bio = estado.bio.trim(),
            )

            if (result.isSuccess) {
                _uiState.update { it.copy(guardando = false, guardado = true, errorMessageRes = null) }
            } else {
                _uiState.update {
                    it.copy(guardando = false, errorMessageRes = mensajeDeError(result.exceptionOrNull()))
                }
            }
        }
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?): Int = when (error) {
        is SinSesionException -> R.string.error_sin_sesion
        is SinConexionException -> R.string.error_sin_conexion
        else -> R.string.error_guardar_perfil
    }
}
