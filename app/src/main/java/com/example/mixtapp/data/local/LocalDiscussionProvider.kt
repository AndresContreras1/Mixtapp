package com.example.mixtapp.data.local

import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.discussion.model.DiscussionCommentUi
import com.example.mixtapp.ui.screens.discussion.model.DiscussionReviewUi
import com.example.mixtapp.ui.screens.discussion.model.DiscussionUi

object LocalDiscussionProvider {

    // Cada discusion se identifica con el mismo id de la resena que la abre,
    // para que la navegacion pueda buscarla por ese id
    val discussions = listOf(
        DiscussionUi(
            review = DiscussionReviewUi(
                id = "toxicity-liz",
                reviewerName = "Liz",
                reviewerAvatarText = "Lz",
                reviewedAt = "4h ago",
                coverRes = R.drawable.toxicity_soad,
                albumTitle = "Toxicity",
                artistName = "System of a down",
                rating = 5,
                reviewText = "\"Toxicity\" is pure chaotic genius. Blending heavy, erratic riffs with Serj Tankian's manic vocals, System of a Down turns societal overload into an insanely catchy, immortal metal anthem.",
                likes = 24,
                commentsCount = 4,
                isLiked = false,
                isShared = false,
            ),
            comments = sharedComments,
        ),
        DiscussionUi(
            review = DiscussionReviewUi(
                id = "from-zero-jhon",
                reviewerName = "Jhon",
                reviewerAvatarText = "Jh",
                reviewedAt = "1 hour ago",
                coverRes = R.drawable.from_zero,
                albumTitle = "From Zero",
                artistName = "Linkin Park",
                rating = 5,
                reviewText = "From Zero is a fiery, seamless rebirth for Linkin Park. Blending raw, heavy nostalgia with fresh, high-voltage energy-fueled by Emily Armstrong's powerhouse vocals-it proves the band can honor their iconic legacy while stepping boldly into a new era.",
                likes = 24,
                commentsCount = 4,
                isLiked = false,
                isShared = false,
            ),
            comments = sharedComments,
        ),
    )
}

// Hilo de comentarios de ejemplo, compartido por las dos discusiones
private val sharedComments = listOf(
    DiscussionCommentUi(
        id = "comment-priya",
        author = "Priya",
        initials = "pk",
        timeAgo = "3h ago",
        content = "My only gripe is that it ends too soon. I wanted 20 more minutes of this world. Is there a deluxe edition or anything?",
        likes = 12,
    ),
    DiscussionCommentUi(
        id = "comment-leo",
        author = "Leo",
        initials = "Lb",
        timeAgo = "2h ago",
        content = "Nothing announced yet sadly. There are some live recordings floating around from the tour though.",
        likes = 4,
        isReply = true,
    ),
    DiscussionCommentUi(
        id = "comment-alex-1",
        author = "Alex",
        initials = "Al",
        timeAgo = "2h ago",
        content = "Hard disagree on the 4 stars tbh. Some of the mid-album tracks drag for me. Still great, but a 3 for me personally.",
        likes = 6,
    ),
    DiscussionCommentUi(
        id = "comment-soph",
        author = "Soph",
        initials = "Sp",
        timeAgo = "1h ago",
        content = "Which tracks specifically? I'm curious because I felt the opposite -- the pacing felt intentional.",
        likes = 4,
    ),
    DiscussionCommentUi(
        id = "comment-alex-2",
        author = "Alex",
        initials = "Al",
        timeAgo = "1h ago",
        content = "Tracks 6 and 7 mostly. Good on their own but slow the momentum imo.",
        likes = 1,
        isReply = true,
    ),
)
