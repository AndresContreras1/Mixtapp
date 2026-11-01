package com.example.mixtapp.ui.screens.profile.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

data class ProfileTabUi(
    val id: String,
    @StringRes val label: Int
)

const val PESTANA_PERFIL = "perfil"
const val PESTANA_GUARDADOS = "guardados"

// Lo mio (favoritos y actividad) y lo que guarde desde el detalle. Cambiar foto no esta aqui: no se
// elige como las demas, solo abre la galeria
val profileTabs = listOf(
    ProfileTabUi(id = PESTANA_PERFIL, label = R.string.nav_profile),
    ProfileTabUi(id = PESTANA_GUARDADOS, label = R.string.profile_tab_guardados)
)
