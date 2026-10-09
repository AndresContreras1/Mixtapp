package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.CreateReviewDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewRetrofitService {

    @POST("reviews")
    suspend fun createReview(@Body review: CreateReviewDto): Unit

    @PUT("reviews/{id}")
    suspend fun updateReview(@Path("id") id: String, @Body review: CreateReviewDto): Unit

    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: String): Unit
}
