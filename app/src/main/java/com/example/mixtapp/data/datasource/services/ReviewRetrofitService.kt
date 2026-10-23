package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ReviewRetrofitService {

    @GET("reviews")
    suspend fun getReviews(): List<ReviewConUsuarioDto>

    @GET("reviews/{id}")
    suspend fun getReviewById(@Path("id") id: String): ReviewConAlbumDto

    @POST("reviews")
    suspend fun createReview(@Body review: CreateReviewDto): Unit

    @PUT("reviews/{id}")
    suspend fun updateReview(@Path("id") id: String, @Body review: CreateReviewDto): Unit

    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: String): Unit
}
