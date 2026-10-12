package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilDto

interface UsuarioRemoteDataSource {
    suspend fun getUsuarioById(id: String): UsuarioPerfilDto
    suspend fun getReviewsDeUsuario(id: String): List<ReviewConAlbumDto>
}
