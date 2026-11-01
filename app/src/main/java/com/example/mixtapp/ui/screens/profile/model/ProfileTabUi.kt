package com.example.mixtapp.ui.screens.profile.model

import androidx.annotation.StringRes
import com.example.mixtapp.R

data class ProfileTabUi(
    val id: String,
    @StringRes val label: Int
)

const val PESTANA_PERFIL = "perfil"
const val PESTANA_GUARDADOS = "guardados"
const val PESTANA_SEGUIDORES = "seguidores"
const val PESTANA_SIGUIENDO = "siguiendo"

// Lo mio (favoritos y actividad), lo que guarde desde el detalle y mis dos listas de usuarios
val profileTabs = listOf(
    ProfileTabUi(id = PESTANA_PERFIL, label = R.string.nav_profile),
    ProfileTabUi(id = PESTANA_GUARDADOS, label = R.string.profile_tab_guardados),
    ProfileTabUi(id = PESTANA_SEGUIDORES, label = R.string.titulo_seguidores),
    ProfileTabUi(id = PESTANA_SIGUIENDO, label = R.string.following_title)
)
