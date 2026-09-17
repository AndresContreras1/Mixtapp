package com.example.mixtapp.data.local

import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.FollowingStoryUi
import com.example.mixtapp.data.model.FollowingUi

object LocalFollowingProvider {
    var following = FollowingUi(
        id = "yourname",
        followingCount = 84,
        followersCount = 312,
        stories = listOf(
            FollowingStoryUi(id = "log", label = "Tu historia", initials = "+", isAddAction = true),
            FollowingStoryUi(id = "jen", label = "Jen", initials = "J", isAddAction = false),
            FollowingStoryUi(id = "jhon", label = "Jhon", initials = "Jh", isAddAction = false),
            FollowingStoryUi(id = "vik", label = "Vik", initials = "V", isAddAction = false),
            FollowingStoryUi(id = "liz", label = "Liz", initials = "Lz", isAddAction = false),
            FollowingStoryUi(id = "bob", label = "Bob", initials = "B", isAddAction = false),
        ),
        reviews = listOf(
            FollowingReviewUi(
                id = "toxicity-liz",
                reviewerName = "Liz",
                reviewerInitials = "Lz",
                reviewedAt = "hace 18 min",
                minutosDesdeLaResena = 18,
                album = LocalAlbumProvider.toxicity,
                rating = 5,
                reviewText = "\"Toxicity\" is pure chaotic genius. Blending heavy, erratic riffs with Serj Tankian's manic vocals, System of a Down turns societal overload into an insanely catchy, immortal metal anthem.",
                likes = 24,
                comments = 1,
                isLiked = false,
                isShared = false,
            ),
            FollowingReviewUi(
                id = "rush-jen",
                reviewerName = "Jen",
                reviewerInitials = "J",
                reviewedAt = "hace 40 min",
                minutosDesdeLaResena = 40,
                album = LocalAlbumProvider.rush,
                rating = 4,
                reviewText = "",
                likes = 9,
                comments = 0,
                isLiked = false,
                isShared = false,
            ),
            FollowingReviewUi(
                id = "from-zero-jhon",
                reviewerName = "Jhon",
                reviewerInitials = "Jh",
                reviewedAt = "hace 1 hora",
                minutosDesdeLaResena = 60,
                album = LocalAlbumProvider.fromZero,
                rating = 5,
                reviewText = "From Zero is a fiery, seamless rebirth for Linkin Park. Blending raw, heavy nostalgia with fresh, high-voltage energy-fueled by Emily Armstrong's powerhouse vocals-it proves the band can honor their iconic legacy while stepping boldly into a new era.",
                likes = 24,
                comments = 1,
                isLiked = false,
                isShared = false,
            ),
        ),
    )
}
