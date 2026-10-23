package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto

interface ReviewRemoteDataSource {
    suspend fun getReviewById(id: String, currentUserId: String = ""): ReviewConAlbumDto
    suspend fun createReview(review: CreateReviewDto): Unit
    suspend fun updateReview(id: String, review: CreateReviewDto): Unit
    suspend fun deleteReview(id: String): Unit
    // Una sola funcion da el like o lo quita, segun si ya existe
    suspend fun sendOrDeleteLike(reviewId: String, userId: String): Unit
}
