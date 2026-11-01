package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.ArtistaDto
import retrofit2.http.GET
import retrofit2.http.Path

// Las rutas de artistas del backend de Express, de solo lectura. Hilt crea el servicio en AppModule
interface ArtistaRetrofitService {

    // Todos los artistas
    @GET("artistas")
    suspend fun getArtistas(): List<ArtistaDto>

    // Un artista; si no existe el backend responde 404
    @GET("artistas/{id}")
    suspend fun getArtistaById(@Path("id") id: String): ArtistaDto
}
