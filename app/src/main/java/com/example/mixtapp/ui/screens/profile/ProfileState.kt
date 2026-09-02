package com.example.mixtapp.ui.screens.profile

import com.example.mixtapp.ui.screens.profile.model.ProfileUi

// El perfil es nulable porque puede que todavia no se haya cargado
data class ProfileState(
    val profile: ProfileUi? = null,
    val selectedTab: String = "Profile",
)
