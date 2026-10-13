package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.Album

object LocalAlbumProvider {

    val teatroDira = Album(
        id = "1",
        title = "Teatro D'ira Vol I",
        artist = "Maneskin",
        cover = AlbumCovers.TEATRO_DIRA,
        year = "2021",
        genre = "Hard Rock",
        tags = listOf("2021", "rock", "Hard Rock"),
    )

    val rush = Album(
        id = "2",
        title = "Rush!",
        artist = "Maneskin",
        cover = AlbumCovers.RUSH,
        year = "2023",
        genre = "Dance Punk",
        tags = listOf("2023", "pop rock", "Dance Punk"),
    )

    val finisterra = Album(
        id = "3",
        title = "Finisterra",
        artist = "Mago de Oz",
        cover = AlbumCovers.FINISTERRA,
        year = "2000",
        genre = "Power Metal",
        tags = listOf("2000", "folk metal", "Power Metal"),
    )

    val fromZero = Album(
        id = "4",
        title = "From Zero",
        artist = "Linkin Park",
        cover = AlbumCovers.FROM_ZERO,
        year = "2024",
        genre = "Rock Alternativo",
        tags = listOf("2024", "nu metal", "Rock Alternativo"),
    )

    val theSharpestLives = Album(
        id = "5",
        title = "The Sharpest Lives",
        artist = "My Chemical Romance",
        cover = AlbumCovers.RUSH,
        year = "2006",
        genre = "Rock Alternativo",
        tags = listOf("2006", "emo", "Rock Alternativo"),
    )

    val toxicity = Album(
        id = "6",
        title = "Toxicity",
        artist = "System of a down",
        cover = AlbumCovers.TOXICITY,
        year = "2001",
        genre = "Nu Metal",
        tags = listOf("2001", "nu metal", "Metal Alternativo"),
    )

    val randomAccessMemories = Album(
        id = "7",
        title = "Random Access Memories",
        artist = "Daft Punk",
        cover = AlbumCovers.RANDOM_ACCESS_MEMORIES,
        year = "2013",
        genre = "Electrónica",
        tags = listOf("2013", "disco", "Electrónica"),
    )

    val theBlackParade = Album(
        id = "8",
        title = "The Black Parade",
        artist = "My Chemical Romance",
        cover = AlbumCovers.THE_BLACK_PARADE,
        year = "2006",
        genre = "Rock Alternativo",
        tags = listOf("2006", "emo", "Rock Alternativo"),
    )

    val albums = listOf(
        teatroDira,
        rush,
        finisterra,
        fromZero,
        theSharpestLives,
        toxicity,
        randomAccessMemories,
        theBlackParade,
    )
}
