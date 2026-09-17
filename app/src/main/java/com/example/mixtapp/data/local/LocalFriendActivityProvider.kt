package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.FriendActivityUi

object LocalFriendActivityProvider {

    val friendActivity = FriendActivityUi(
        id = "random-access-memories",
        album = LocalAlbumProvider.randomAccessMemories,
        rating = 5,
        quote = "A masterpiece from start to finish.",
    )
}
