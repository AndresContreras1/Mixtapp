package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import retrofit2.http.GET
import retrofit2.http.Path

// Las rutas de albumes del backend de Express. Hilt crea el servicio en AppModule
interface AlbumRetrofitService {

    // Todos los albumes
    @GET("albumes")
    suspend fun getAlbumes(): List<AlbumDto>

    // El detalle de un album; si no existe el backend responde 404
    @GET("albumes/{id}")
    suspend fun getAlbumById(@Path("id") id: String): AlbumDto

    // Las resenas de un album, con el usuario que las escribio (include)
    @GET("albumes/{id}/reviews")
    suspend fun getReviewsDeAlbum(@Path("id") id: String): List<ReviewConUsuarioDto>
}
