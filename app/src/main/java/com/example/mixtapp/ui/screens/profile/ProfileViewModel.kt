package com.example.mixtapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalProfileProvider
import com.example.mixtapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

// Pestanas de la pantalla de perfil
val profileTabs = listOf("Profile", "Diary", "Lists", "Library")

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository
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

        _uiState.update {
            it.copy(
                profile = LocalProfileProvider.profile,
                tabs = profileTabs,
                selectedTab = profileTabs.first(),
                usuario = usuario,
            )
        }
    }

    fun updateSelectedTab(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }

    // signOut no lleva suspend: solo borra la sesion del celular, no va a la red
    fun cerrarSesion() {
        authRepository.signOut()
        _uiState.update { it.copy(sesionCerrada = true) }
    }
}
