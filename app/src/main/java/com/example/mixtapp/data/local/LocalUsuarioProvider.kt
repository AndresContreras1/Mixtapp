package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.UsuarioUi

// Solo para las previews: la app lee los usuarios de la base remota
object LocalUsuarioProvider {

    val usuario = UsuarioUi(
        id = "2",
        seguidores = 12,
        seguidos = 34,
        nombre = "Mateo Gómez",
        username = "mateo",
        bio = "Rock alternativo y conciertos en vivo",
        fotoUrl = "",
    )

    // Para la lista de seguidores y seguidos
    val usuarios = listOf(
        usuario,
        UsuarioUi(
            id = "3",
            nombre = "Laura Pérez",
            username = "laura",
            bio = "",
            fotoUrl = "",
        ),
        UsuarioUi(
            id = "4",
            nombre = "Andrés Ruiz",
            username = "andres",
            bio = "",
            fotoUrl = "",
        ),
    )
}
