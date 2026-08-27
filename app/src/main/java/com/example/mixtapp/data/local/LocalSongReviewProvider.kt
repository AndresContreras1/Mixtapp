package com.example.mixtapp.data.local

import com.example.mixtapp.R

data class SongReviewUi(
    val coverRes: Int,
    val tags: List<String>,
    val title: String,
    val artist: String,
    val rating: String,
    val ratingCount: String,
    val recommendRate: String,
    val userRating: Int,
    val isSaved: Boolean,
    val isLiked: Boolean,
    val reviews: List<SongReviewItemUi>,
)

data class SongReviewItemUi(
    val author: String,
    val daysAgo: String,
    val rating: Int,
    val content: String,
    val likes: Int,
)

object LocalSongReviewProvider {
    val songReview = SongReviewUi(
        coverRes = R.drawable.rush_portada,
        tags = listOf("2006", "emo", "Rock Alternativo"),
        title = "The Sharpest Lives",
        artist = "My Chemical Romance",
        rating = "4.5",
        ratingCount = "28.4k",
        recommendRate = "94%",
        userRating = 0,
        isSaved = false,
        isLiked = false,
        reviews = listOf(
            SongReviewItemUi(
                author = "Priya",
                daysAgo = "3d ago",
                rating = 5,
                content = "\"The Sharpest Lives\" is an absolute rush on The Black Parade, blending dark, frantic energy with an insanely catchy hook. Gerard Way's theatrical vocals and the sharp guitar work turn chaotic self-destruction into one of My Chemical Romance's most addictive anthems.",
                likes = 24,
            )
        ),
    )
}
