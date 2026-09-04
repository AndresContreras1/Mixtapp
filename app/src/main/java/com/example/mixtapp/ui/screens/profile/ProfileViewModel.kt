package com.example.mixtapp.ui.screens.profile

import androidx.lifecycle.ViewModel
import com.example.mixtapp.data.local.LocalProfileProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// Pestanas de la pantalla de perfil
val profileTabs = listOf("Profile", "Diary", "Lists", "Library")

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getProfile()
    }

    private fun getProfile() {
        _uiState.update {
            it.copy(
                profile = LocalProfileProvider.profile,
                tabs = profileTabs,
                selectedTab = profileTabs.first(),
            )
        }
    }

    fun updateSelectedTab(tab: String) {
        _uiState.update { it.copy(selectedTab = tab) }
    }
}
