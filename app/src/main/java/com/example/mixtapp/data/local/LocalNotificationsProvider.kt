package com.example.mixtapp.data.local

import com.example.mixtapp.ui.screens.notifications.model.NotificationUi

object LocalNotificationsProvider {

    const val TAB_ALL = "All"
    const val TAB_UNREAD = "Unread"

    val tabs = listOf(TAB_ALL, TAB_UNREAD)

    val notifications = listOf(
        NotificationUi(
            id = "1",
            actorInitials = "KA",
            actorName = "Kai",
            message = "le dio me gusta a tu reseña de Maneskin",
            highlightedWord = "Maneskin",
            timeAgo = "hace 4 min",
            thumbnail = AlbumCovers.TEATRO_DIRA,
            isRead = false,
            section = "Hoy",
        ),
        NotificationUi(
            id = "2",
            actorInitials = "DR",
            actorName = "Daniela",
            message = "empezó a seguirte",
            highlightedWord = null,
            timeAgo = "hace 4 min",
            thumbnail = null,
            isRead = false,
            section = "Hoy",
        ),
        NotificationUi(
            id = "3",
            actorInitials = "MS",
            actorName = "Mae",
            message = "comentó tu reseña de Maneskin",
            highlightedWord = "Maneskin",
            timeAgo = "hace 32 min",
            thumbnail = AlbumCovers.TEATRO_DIRA,
            isRead = false,
            section = "Hoy",
        ),
        NotificationUi(
            id = "4",
            actorInitials = "NR",
            actorName = "Nico",
            message = "respondió a tu comentario - \"Sí, con ese contexto pega todavía más al reescucharlo.\" Maneskin",
            highlightedWord = "Maneskin",
            timeAgo = "hace 32 min",
            thumbnail = AlbumCovers.TEATRO_DIRA,
            isRead = false,
            section = "Hoy",
        ),
        NotificationUi(
            id = "5",
            actorInitials = "MX",
            actorName = "MixtApp",
            message = "¡Tu reseña de Maneskin llegó a 50 me gusta!",
            highlightedWord = "Maneskin",
            timeAgo = "hace 2 h",
            thumbnail = AlbumCovers.TEATRO_DIRA,
            isRead = true,
            section = "Hoy",
        ),
        NotificationUi(
            id = "6",
            actorInitials = "KA",
            actorName = "Kai",
            message = "le dio me gusta a tu reseña de Maneskin",
            highlightedWord = "Maneskin",
            timeAgo = "hace 1 día",
            thumbnail = AlbumCovers.TEATRO_DIRA,
            isRead = true,
            section = "Antes",
        ),
        NotificationUi(
            id = "7",
            actorInitials = "DR",
            actorName = "Daniela",
            message = "empezó a seguirte",
            highlightedWord = null,
            timeAgo = "hace 2 días",
            thumbnail = null,
            isRead = true,
            section = "Antes",
        ),
    )
}