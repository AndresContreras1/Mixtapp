package com.example.mixtapp.ui.screens.profile

import android.net.Uri
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalProfileProvider
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.SinSesionException
import com.example.mixtapp.data.repository.StorageRepository
import com.example.mixtapp.ui.screens.profile.model.profileTabs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository
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

        _uiState.update {
            it.copy(
                profile = LocalProfileProvider.profile,
                tabs = profileTabs,
                selectedTabId = profileTabs.first().id,
                usuario = usuario,
                profileImageUrl = foto,
            )
        }
    }

    fun subirFotoDePerfil(uri: Uri) {
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
    private fun mensajeDeError(error: Throwable?): Int = when (error) {
        is SinSesionException -> R.string.error_sin_sesion
        else -> R.string.error_subir_imagen
    }

    fun updateSelectedTab(tabId: String) {
        _uiState.update { it.copy(selectedTabId = tabId) }
    }

    // signOut no lleva suspend: solo borra la sesion del celular, no va a la red
    fun cerrarSesion() {
        authRepository.signOut()
        _uiState.update { it.copy(sesionCerrada = true) }
    }
}
