package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.FriendActivityUi

// Solo para la preview del inicio: la actividad real sale de las resenas del backend
object LocalFriendActivityProvider {

    val friendActivity = FriendActivityUi(
        id = "random-access-memories",
        album = LocalAlbumProvider.randomAccessMemories,
        rating = 5,
        quote = "A masterpiece from start to finish.",
        author = "Camila Herrera",
        date = "2026-08-07",
    )
}
