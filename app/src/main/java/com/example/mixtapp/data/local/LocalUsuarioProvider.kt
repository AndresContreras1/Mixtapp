package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.UsuarioUi

// Solo para las previews: la app lee los usuarios del backend
object LocalUsuarioProvider {

    val usuario = UsuarioUi(
        id = "2",
        nombre = "Mateo Gómez",
        email = "mateo@example.com",
        fotoUrl = "",
    )
}
