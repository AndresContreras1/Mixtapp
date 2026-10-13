package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.UsuarioUi

// Solo para las previews: la app lee los usuarios de la base remota
object LocalUsuarioProvider {

    val usuario = UsuarioUi(
        id = "2",
        nombre = "Mateo Gómez",
        username = "mateo",
        bio = "Rock alternativo y conciertos en vivo",
        fotoUrl = "",
    )
}
