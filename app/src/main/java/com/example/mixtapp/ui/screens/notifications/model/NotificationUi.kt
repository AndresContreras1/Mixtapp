package com.example.mixtapp.ui.screens.notifications.model

import androidx.annotation.DrawableRes

data class NotificationUi(
    val id: String,
    val actorInitials: String,
    val actorName: String,          // parte en negrita al inicio: "Kai", "Daniela", "MixtApp"...
    val message: String,            // resto de la oracion completa
    val highlightedWord: String?,   // palabra dentro de "message" que tambien va en negrita, ej. "Maneskin"
    val timeAgo: String,
    @DrawableRes val thumbnailRes: Int?,
    val isRead: Boolean,
    val section: String,            // "Today" / "Earlier", para agrupar en la lista
)