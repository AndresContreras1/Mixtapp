package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioDtoGeneric

// Contrato de los usuarios: cada implementacion retorna el DTO de su propia base
interface UsuarioRemoteDataSource {
    suspend fun getUsuarioById(id: String): UsuarioDtoGeneric
    suspend fun getReviewsDeUsuario(id: String): List<ReviewConAlbumDto>
    // Crea o reemplaza los datos del usuario que tiene ese id
    suspend fun registrarUsuario(registrarUsuarioDto: RegistrarUsuarioDto, usuarioId: String): Unit
}
