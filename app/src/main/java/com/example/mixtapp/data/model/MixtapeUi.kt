package com.example.mixtapp.data.model

// Una mixtape como la pinta su tarjeta: el perfil, Agregar a mixtape y la cabecera del detalle
data class MixtapeUi(
    val id: String,
    val nombre: String,
    // Vacia si no le puso
    val descripcion: String,
    // Quien la creo, para "Creada por"
    val usuarioNombre: String,
    val usuarioFotoUrl: String,
    val itemsCount: Int,
    // Las portadas de los primeros items, hasta cuatro
    val portadas: List<String>,
    // Solo quien la creo le puede quitar items o borrarla
    val isMine: Boolean,
    // Si ya tiene el item que se quiere agregar; solo lo usa Agregar a mixtape
    val tieneElItem: Boolean,
)

// El detalle: la mixtape con todos sus items, en el orden en que se agregaron
data class MixtapeConItemsUi(
    val mixtape: MixtapeUi,
    val items: List<ItemResumenUi>,
)
