package com.example.mixtapp.ui.screens.profile.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

data class ProfileTabUi(
    val id: String,
    @StringRes val label: Int
)

const val PESTANA_PERFIL = "perfil"
const val PESTANA_DIARIO = "diario"
const val PESTANA_LISTAS = "listas"
const val PESTANA_BIBLIOTECA = "biblioteca"

val profileTabs = listOf(
    ProfileTabUi(id = PESTANA_PERFIL, label = R.string.profile_tab_profile),
    ProfileTabUi(id = PESTANA_DIARIO, label = R.string.profile_tab_diary),
    ProfileTabUi(id = PESTANA_LISTAS, label = R.string.profile_tab_lists),
    ProfileTabUi(id = PESTANA_BIBLIOTECA, label = R.string.profile_tab_library)
)
