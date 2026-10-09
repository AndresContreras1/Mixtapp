package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioDto

interface UsuarioRemoteDataSource {
    suspend fun getUsuarioById(id: String): UsuarioDto
    suspend fun getReviewsDeUsuario(id: String): List<ReviewConAlbumDto>
}
