package com.example.mixtapp.data.model

// Lo que la Cloud Function guarda en el campo tipo de cada notificacion
const val NOTIFICACION_LIKE = "like"
const val NOTIFICACION_COMENTARIO = "comentario"
const val NOTIFICACION_RESPUESTA = "respuesta"
const val NOTIFICACION_SEGUIDOR = "seguidor"

data class NotificationUi(
    val id: String,
    val tipo: String,
    // Quien dio el like, comento o empezo a seguirme
    val actorId: String,
    val actorName: String,
    val actorImage: String,
    // El album, la cancion o el artista de la resena; vacio en un seguidor nuevo
    val itemTitle: String,
    val itemImage: String,
    // La resena que hay que abrir al tocarla; null en un seguidor nuevo
    val reviewId: String?,
    // Cuanto hace que llego ("3 h"); vacio si fue hace menos de un minuto
    val timeAgo: String,
    val isRead: Boolean,
    // Si llego hoy: la pantalla las agrupa en Hoy y Antes
    val isToday: Boolean,
)
