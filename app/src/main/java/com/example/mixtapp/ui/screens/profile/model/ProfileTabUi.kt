package com.example.mixtapp.ui.screens.profile.model

import com.example.mixtapp.R
import com.example.mixtapp.ui.utils.OpcionUi

const val PESTANA_PERFIL = "perfil"
const val PESTANA_GUARDADOS = "guardados"
const val PESTANA_MIXTAPES = "mixtapes"

// Lo mio (favoritos y actividad), lo que guarde desde el detalle y mis mixtapes. Cambiar foto no
// esta aqui: no se elige como las demas, solo abre la galeria
val profileTabs = listOf(
    OpcionUi(id = PESTANA_PERFIL, label = R.string.nav_profile),
    OpcionUi(id = PESTANA_GUARDADOS, label = R.string.profile_tab_guardados),
    OpcionUi(id = PESTANA_MIXTAPES, label = R.string.profile_tab_mixtapes),
)
