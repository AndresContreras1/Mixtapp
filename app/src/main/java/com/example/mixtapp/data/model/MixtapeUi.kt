package com.example.mixtapp.data.model

// Una mixtape como la pintan el perfil y su detalle
data class MixtapeUi(
    val id: String,
    val nombre: String,
    // Vacia si no le puso
    val descripcion: String,
    val usuarioId: String,
    val usuarioNombre: String,
    val usuarioFotoUrl: String,
    val items: List<ItemResumenUi>,
    // Solo quien la creo le puede quitar items o borrarla
    val isMine: Boolean,
    // Cuanto hace que la creo ("3 d"); vacio si fue hace menos de un minuto
    val timeAgo: String,
) {
    val itemCount: Int get() = items.size
}
