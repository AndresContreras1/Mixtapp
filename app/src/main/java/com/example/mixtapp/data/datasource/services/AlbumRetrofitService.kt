package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import retrofit2.http.GET
import retrofit2.http.Path

interface AlbumRetrofitService {

    @GET("albumes")
    suspend fun getAlbumes(): List<AlbumDto>

    @GET("albumes/{id}")
    suspend fun getAlbumById(@Path("id") id: String): AlbumDto

    @GET("albumes/{id}/reviews")
    suspend fun getReviewsDeAlbum(@Path("id") id: String): List<ReviewConUsuarioDto>
}
