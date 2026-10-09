package com.example.mixtapp.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.CuotaExcedidaException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.PermisoDenegadoException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.SinSesionException
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.data.repository.StorageRepository
import com.example.mixtapp.ui.screens.profile.model.profileTabs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val socialRepository: SocialRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getProfile()
    }

    private fun getProfile() {
        // Recortar el correo es logica, asi que se hace aqui y no en el componente
        val usuario = authRepository.currentUser?.email?.substringBefore("@") ?: ""
        val foto = authRepository.currentUser?.photoUrl?.toString() ?: ""

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val perfilPendiente = async { socialRepository.getProfile() }
            val resenasPendientes = async { reviewRepository.getMyReviews() }

            val result = perfilPendiente.await()
            val resenas = resenasPendientes.await()

            if (result.isSuccess && resenas.isSuccess) {
                _uiState.update {
                    it.copy(
                        profile = result.getOrNull(),
                        misResenas = resenas.getOrNull() ?: emptyList(),
                        isLoading = false,
                        tabs = profileTabs,
                        selectedTabId = profileTabs.first().id,
                        usuario = usuario,
                        profileImageUrl = foto,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = mensajeDeCarga(
                            result.exceptionOrNull() ?: resenas.exceptionOrNull()
                        ),
                    )
                }
            }
        }
    }

    fun subirFotoDePerfil(uri: Uri) {
        Log.d("ProfileViewModel", uri.toString())

        viewModelScope.launch {
            _uiState.update { it.copy(subiendoImagen = true, errorImagenRes = null) }

            val result = storageRepository.uploadProfileImage(uri = uri)

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        subiendoImagen = false,
                        profileImageUrl = result.getOrNull() ?: "",
                        errorImagenRes = null,
                    )
                }
            } else {
                Log.d("ProfileViewModel", result.exceptionOrNull()?.message.toString())

                _uiState.update {
                    it.copy(
                        subiendoImagen = false,
                        errorImagenRes = mensajeDeError(result.exceptionOrNull()),
                    )
                }
            }
        }
    }

    @StringRes
    private fun mensajeDeCarga(error: Throwable?): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> R.string.error_cargar_contenido
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?): Int = when (error) {
        is SinSesionException -> R.string.error_sin_sesion
        is SinConexionException -> R.string.error_sin_conexion
        is CuotaExcedidaException -> R.string.error_cuota_excedida
        is PermisoDenegadoException -> R.string.error_permiso_denegado
        else -> R.string.error_subir_imagen
    }

    fun updateSelectedTab(tabId: String) {
        _uiState.update { it.copy(selectedTabId = tabId) }
    }

    // signOut no lleva suspend: solo borra la sesion del celular, no va a la red
    fun cerrarSesion() {
        authRepository.signOut()
    }
}
