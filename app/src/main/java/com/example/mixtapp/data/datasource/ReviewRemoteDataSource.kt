package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.CreateReviewDto

interface ReviewRemoteDataSource {
    suspend fun createReview(review: CreateReviewDto): Unit
    suspend fun updateReview(id: String, review: CreateReviewDto): Unit
    suspend fun deleteReview(id: String): Unit?
}
