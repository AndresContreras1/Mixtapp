package com.example.mixtapp.data.model

// Lo que esta escuchando ahora un usuario que sigo (o yo), con las reacciones 🔥 que lleva
data class SonandoUi(
    val usuarioId: String,
    val nombre: String,
    val fotoUrl: String,
    val item: ItemResumenUi,
    // Cuanto hace que lo puso ("3 h"); vacio si fue hace menos de un minuto
    val timeAgo: String,
    val reacciones: Int,
    // Si ya le reaccione a lo que esta escuchando ahora
    val reaccionado: Boolean,
    // Lo mio va primero en la fila y se puede quitar; a lo mio no se le reacciona
    val esMio: Boolean,
)
