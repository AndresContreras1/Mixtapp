package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.dtos.ReviewDto

// Los usuarios de Firebase no existen en el backend: las implementaciones de Retrofit queman al
// usuario de la app como el 1, como pide el sprint 9
const val USUARIO_DE_LA_APP_EN_EL_BACKEND = "1"

// En el backend el usuario de la app es el 1: sus resenas vuelven con el uid de la sesion para que
// el repositorio las reconozca como mias
fun ReviewDto.conMiUsuario(currentUserId: String): ReviewDto =
    if (currentUserId.isNotEmpty() && usuarioId == USUARIO_DE_LA_APP_EN_EL_BACKEND) {
        copy(usuarioId = currentUserId)
    } else {
        this
    }

// Lo mismo para una lista de resenas
fun List<ReviewDto>.conMiUsuario(currentUserId: String): List<ReviewDto> =
    map { review -> review.conMiUsuario(currentUserId) }
