package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.SongReviewItemUi
import com.example.mixtapp.data.model.SongReviewUi

object LocalSongReviewProvider {

    val songs = mutableListOf(
        SongReviewUi(
            album = LocalAlbumProvider.teatroDira,
            rating = 4.8,
            ratingCount = "41.2k",
            recommendRate = 97,
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-1",
                    isLiked = false,
                    author = "Priya",
                    daysAgo = "hace 2 días",
                    rating = 5,
                    content = "Teatro d'ira: Vol. I captures Maneskin at their absolute peak. Razor-sharp riffs, explosive live energy and Damiano's theatrical vocals turn pure attitude into a tight, unforgettable record.",
                    likes = 31,
                )
            ),
        ),
        SongReviewUi(
            album = LocalAlbumProvider.rush,
            rating = 4.2,
            ratingCount = "33.7k",
            recommendRate = 89,
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-2",
                    isLiked = false,
                    author = "Leo",
                    daysAgo = "hace 5 días",
                    rating = 4,
                    content = "An infectious, high-octane pop-rock spectacle built for global arenas. It trades some of the raw Italian grit for polished dance-punk grooves, but the attitude never drops.",
                    likes = 18,
                )
            ),
        ),
        SongReviewUi(
            album = LocalAlbumProvider.finisterra,
            rating = 4.7,
            ratingCount = "22.9k",
            recommendRate = 95,
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-3",
                    isLiked = false,
                    author = "Alex",
                    daysAgo = "hace 1 semana",
                    rating = 5,
                    content = "Mago de Oz's ambitious folk metal masterpiece. Power metal riffs blended with Celtic flutes and violins turn an epic medieval concept into a legendary album.",
                    likes = 27,
                )
            ),
        ),
        SongReviewUi(
            album = LocalAlbumProvider.fromZero,
            rating = 4.4,
            ratingCount = "58.1k",
            recommendRate = 92,
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-4",
                    isLiked = false,
                    author = "Soph",
                    daysAgo = "hace 4 días",
                    rating = 4,
                    content = "From Zero strikes a balance between heavy nostalgia and genuine reinvention. Emily Armstrong commands every track with raw power, signaling a confident new chapter.",
                    likes = 42,
                )
            ),
        ),
        SongReviewUi(
            album = LocalAlbumProvider.theSharpestLives,
            rating = 4.5,
            ratingCount = "28.4k",
            recommendRate = 94,
            userRating = 0,
            isSaved = false,
            isLiked = false,
            reviews = listOf(
                SongReviewItemUi(
                    id = "review-5",
                    isLiked = false,
                    author = "Priya",
                    daysAgo = "hace 3 días",
                    rating = 5,
                    content = "\"The Sharpest Lives\" is an absolute rush on The Black Parade, blending dark, frantic energy with an insanely catchy hook. Gerard Way's theatrical vocals and the sharp guitar work turn chaotic self-destruction into one of My Chemical Romance's most addictive anthems.",
                    likes = 24,
                )
            ),
        ),
    )

    // Los tres albumes que se muestran en la pantalla principal
    val popularSongs: List<SongReviewUi>
        get() = songs.take(3)

    // El album destacado de la pantalla principal
    val trendingSong: SongReviewUi
        get() = songs[2]
}
