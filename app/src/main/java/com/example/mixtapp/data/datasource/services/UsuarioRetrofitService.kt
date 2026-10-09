package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UsuarioRetrofitService {

    @GET("usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: String): UsuarioDto

    @GET("usuarios/{id}/reviews")
    suspend fun getReviewsDeUsuario(@Path("id") id: String): List<ReviewConAlbumDto>
}
