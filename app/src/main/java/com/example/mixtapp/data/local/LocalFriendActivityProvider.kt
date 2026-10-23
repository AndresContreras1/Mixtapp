package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.FriendActivityUi

// Solo para las previews del inicio: la actividad real sale de las resenas guardadas
object LocalFriendActivityProvider {

    val friendActivity = FriendActivityUi(
        id = "random-access-memories",
        album = LocalAlbumProvider.randomAccessMemories,
        rating = 5,
        quote = "A masterpiece from start to finish.",
        authorId = "5",
        author = "Camila Herrera",
        date = "2026-08-07",
    )

    // Para el filtro de seguidos, que muestra todas las resenas de quienes sigo
    val friendActivities = listOf(
        friendActivity,
        FriendActivityUi(
            id = "toxicity",
            album = LocalAlbumProvider.toxicity,
            rating = 4,
            quote = "Caótico y pegajoso de principio a fin.",
            authorId = "3",
            author = "Laura Pérez",
            date = "2026-08-01",
        ),
    )
}
