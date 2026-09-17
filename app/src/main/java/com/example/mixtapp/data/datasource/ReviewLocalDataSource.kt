package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.local.LocalReviewAlbumProvider
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.MyReviewUi
import javax.inject.Inject

class ReviewLocalDataSource @Inject constructor() {

    suspend fun getMyReviews(): List<MyReviewUi> = LocalMyReviewsProvider.reviews

    suspend fun getDiscussionByReviewId(reviewId: String): DiscussionUi? =
        LocalDiscussionProvider.discussions.find { it.id == reviewId }

    suspend fun getMoods(): List<String> = LocalReviewAlbumProvider.moods

    suspend fun getFechaEscuchaInicial(): String = LocalReviewAlbumProvider.fechaEscuchaInicial

    suspend fun getFechaEscuchaSugerida(): String = LocalReviewAlbumProvider.fechaEscuchaSugerida
}
