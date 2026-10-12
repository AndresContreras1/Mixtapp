package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.data.model.DiscussionUi
import javax.inject.Inject

// Las discusiones siguen locales. Las funciones son suspend como las remotas: si estos datos
// pasan al backend, el ViewModel no cambia
class ReviewLocalDataSource @Inject constructor() {

    suspend fun getDiscussionByReviewId(reviewId: String): DiscussionUi? =
        LocalDiscussionProvider.discussions.find { it.id == reviewId }

    suspend fun guardarDiscusion(discusion: DiscussionUi): Unit {
        val indice = LocalDiscussionProvider.discussions.indexOfFirst { it.id == discusion.id }

        LocalDiscussionProvider.discussions[indice] = discusion
    }
}
