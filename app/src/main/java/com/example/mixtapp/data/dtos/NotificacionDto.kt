package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.NotificationUi
import java.time.Instant

// Igual al documento que la Cloud Function guarda en usuarios/{uid}/notificaciones
data class NotificacionDto(
    val id: String,
    // like, comentario, respuesta o seguidor
    val tipo: String,
    // Copia de quien hizo la accion, del momento en que la hizo
    val actorId: String,
    val actorNombre: String,
    val actorFotoUrl: String?,
    // La resena del like o del comentario; null en un seguidor nuevo
    val reviewId: String?,
    val itemTitulo: String,
    val imagenUrl: String?,
    val leida: Boolean,
    // En formato ISO
    val createdAt: String?,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", "", null, null, "", null, false, null)
}

// hoy llega como AAAA-MM-DD para comparar con la fecha de la notificacion
fun NotificacionDto.toNotificationUi(hoy: String, ahora: Instant = Instant.now()): NotificationUi {
    val fecha = createdAt?.take(10) ?: ""

    return NotificationUi(
        id = id,
        tipo = tipo,
        actorId = actorId,
        actorName = actorNombre,
        actorImage = actorFotoUrl ?: "",
        itemTitle = itemTitulo,
        itemImage = imagenUrl ?: "",
        reviewId = reviewId,
        timeAgo = haceCuanto(fechaIso = createdAt, ahora = ahora),
        isRead = leida,
        isToday = fecha == hoy,
    )
}
