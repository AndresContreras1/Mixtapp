package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.CreateReviewRetrofitDto
import com.example.mixtapp.data.dtos.ReviewRetrofitDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// El CRUD de resenas del backend de Express. Hilt crea el servicio en AppModule
interface ReviewRetrofitService {

    // Todas las resenas, con su usuario, para el inicio
    @GET("reviews")
    suspend fun getReviews(): List<ReviewRetrofitDto>

    // Una resena con su usuario y su album en la misma consulta
    @GET("reviews/{id}")
    suspend fun getReviewById(@Path("id") id: String): ReviewRetrofitDto

    // Crea la resena con el id del usuario, el del album y sus datos
    @POST("reviews")
    suspend fun createReview(@Body review: CreateReviewRetrofitDto): Unit

    // Modifica la calificacion, el comentario y la fecha de escucha
    @PUT("reviews/{id}")
    suspend fun updateReview(@Path("id") id: String, @Body review: CreateReviewRetrofitDto): Unit

    // El backend responde un JSON y no un 204 vacio, que haria fallar a Retrofit
    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: String): Unit
}
