package com.example.mixtapp.data.model

data class NotificationUi(
    val id: String,
    val actorInitials: String,
    val actorName: String,          // parte en negrita al inicio: "Kai", "Daniela", "MixtApp"...
    val message: String,            // resto de la oracion completa
    val highlightedWord: String,    // palabra de "message" que tambien va en negrita, ej. "Maneskin"; "" si no hay
    val timeAgo: String,
    val thumbnail: String,          // portada del album; "" si la notificacion no tiene
    val isRead: Boolean,
    val section: String,            // "Hoy" / "Antes", para agrupar en la lista
)