package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.services.ReviewRetrofitService
import com.example.mixtapp.data.dtos.CreateReviewDto
import javax.inject.Inject

class ReviewRetrofitDataSourceImpl @Inject constructor(
    private val service: ReviewRetrofitService
) : ReviewRemoteDataSource {

    // En el backend la resena siempre queda a nombre del usuario 1
    override suspend fun createReview(review: CreateReviewDto) {
        service.createReview(review.copy(usuarioId = USUARIO_DE_LA_APP_EN_EL_BACKEND))
    }

    override suspend fun updateReview(id: String, review: CreateReviewDto) {
        service.updateReview(id, review.copy(usuarioId = USUARIO_DE_LA_APP_EN_EL_BACKEND))
    }

    override suspend fun deleteReview(id: String): Unit? {
        return service.deleteReview(id)
    }
}
