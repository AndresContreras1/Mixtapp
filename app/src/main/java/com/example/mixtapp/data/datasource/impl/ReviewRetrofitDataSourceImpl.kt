package com.example.mixtapp.data.datasource.impl

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.services.ReviewRetrofitService
import com.example.mixtapp.data.dtos.CreateReviewDto
import javax.inject.Inject

class ReviewRetrofitDataSourceImpl @Inject constructor(
    private val service: ReviewRetrofitService
) : ReviewRemoteDataSource {

    override suspend fun createReview(review: CreateReviewDto) {
        service.createReview(review)
    }

    override suspend fun updateReview(id: String, review: CreateReviewDto) {
        service.updateReview(id, review)
    }

    override suspend fun deleteReview(id: String): Unit? {
        return service.deleteReview(id)
    }
}
