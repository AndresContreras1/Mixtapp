package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.CancionDto
import retrofit2.http.GET
import retrofit2.http.Path

// Las rutas de canciones del backend de Express, de solo lectura. Hilt crea el servicio en AppModule
interface CancionRetrofitService {

    // Todas las canciones
    @GET("canciones")
    suspend fun getCanciones(): List<CancionDto>

    // Una cancion; si no existe el backend responde 404
    @GET("canciones/{id}")
    suspend fun getCancionById(@Path("id") id: String): CancionDto
}
