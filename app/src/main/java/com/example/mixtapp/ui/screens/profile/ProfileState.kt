package com.example.mixtapp.ui.screens.profile

import com.example.mixtapp.ui.screens.profile.model.ProfileUi

// El perfil es nulable porque puede que todavia no se haya cargado
data class ProfileState(
    val profile: ProfileUi? = null,
    // Las pestanas las provee el ViewModel; el componente solo las pinta
    val tabs: List<String> = emptyList(),
    val selectedTab: String = "",
)
