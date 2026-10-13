package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilRetrofitDto
import retrofit2.http.GET
import retrofit2.http.Path

interface UsuarioRetrofitService {

    @GET("usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: String): UsuarioPerfilRetrofitDto

    @GET("usuarios/{id}/reviews")
    suspend fun getReviewsDeUsuario(@Path("id") id: String): List<ReviewConAlbumDto>
}
