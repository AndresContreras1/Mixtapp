package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.BusquedaDeezerDto
import retrofit2.http.GET
import retrofit2.http.Query

// La busqueda publica de Deezer, sin llave: de aqui sale el fragmento de 30 segundos que suena en la
// app. Hilt crea el servicio en AppModule con su propia URL
interface DeezerRetrofitService {

    // Las canciones que coinciden con el texto (artista y titulo), la mas parecida primero
    @GET("search")
    suspend fun buscarCanciones(
        @Query("q") busqueda: String,
        @Query("limit") limite: Int,
    ): BusquedaDeezerDto
}
