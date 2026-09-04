package com.example.mixtapp.data.local

import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.following.model.FollowingReviewUi
import com.example.mixtapp.ui.screens.following.model.FollowingStoryUi
import com.example.mixtapp.ui.screens.following.model.FollowingUi

object LocalFollowingProvider {
    val following = FollowingUi(
        followingCount = 84,
        followersCount = 312,
        filters = listOf("All", "Reviews", "Ratings", "Lists"),
        stories = listOf(
            FollowingStoryUi(id = "log", label = "Your log", initials = "+", isAddAction = true),
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
                reviewedAt = "18 min ago",
                coverRes = R.drawable.toxicity_soad,
                albumTitle = "Toxicity",
                artistName = "System of a down",
                rating = 5,
                reviewText = "\"Toxicity\" is pure chaotic genius. Blending heavy, erratic riffs with Serj Tankian's manic vocals, System of a Down turns societal overload into an insanely catchy, immortal metal anthem.",
                likes = 24,
                comments = 1,
                isLiked = false,
                isShared = false,
            ),
            FollowingReviewUi(
                id = "from-zero-jhon",
                reviewerName = "Jhon",
                reviewerInitials = "Jh",
                reviewedAt = "1 hour ago",
                coverRes = R.drawable.from_zero,
                albumTitle = "From Zero",
                artistName = "Linkin Park",
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
