package com.example.mixtapp.data.datasource.services

import com.example.mixtapp.data.dtos.ReviewRetrofitDto
import com.example.mixtapp.data.dtos.UsuarioPerfilRetrofitDto
import retrofit2.http.GET
import retrofit2.http.Path

// Las lecturas de usuarios del backend de Express. Hilt crea el servicio en AppModule
interface UsuarioRetrofitService {

    // Todos los usuarios: la busqueda filtra en la app
    @GET("usuarios")
    suspend fun getUsuarios(): List<UsuarioPerfilRetrofitDto>

    // Un usuario por id; si no existe el backend responde 404
    @GET("usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: String): UsuarioPerfilRetrofitDto

    // Las resenas de un usuario, con el album y el usuario (include)
    @GET("usuarios/{id}/reviews")
    suspend fun getReviewsDeUsuario(@Path("id") id: String): List<ReviewRetrofitDto>
}
