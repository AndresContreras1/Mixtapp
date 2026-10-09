package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.data.local.LocalReviewAlbumProvider
import com.example.mixtapp.data.model.DiscussionUi
import javax.inject.Inject

class ReviewLocalDataSource @Inject constructor() {

    suspend fun getDiscussionByReviewId(reviewId: String): DiscussionUi? =
        LocalDiscussionProvider.discussions.find { it.id == reviewId }

    suspend fun getMoods(): List<String> = LocalReviewAlbumProvider.moods

    suspend fun getFechaEscuchaInicial(): String = LocalReviewAlbumProvider.fechaEscuchaInicial

    suspend fun getFechaEscuchaSugerida(): String = LocalReviewAlbumProvider.fechaEscuchaSugerida

    suspend fun guardarDiscusion(discusion: DiscussionUi) {
        val indice = LocalDiscussionProvider.discussions.indexOfFirst { it.id == discusion.id }

        LocalDiscussionProvider.discussions[indice] = discusion
    }
}
