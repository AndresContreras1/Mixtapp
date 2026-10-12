package com.example.mixtapp.data.datasource.impl

import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.datasource.services.UsuarioRetrofitService
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilDto
import javax.inject.Inject

class UsuarioRetrofitDataSourceImpl @Inject constructor(
    private val service: UsuarioRetrofitService
) : UsuarioRemoteDataSource {

    override suspend fun getUsuarioById(id: String): UsuarioPerfilDto {
        return service.getUsuarioById(id)
    }

    override suspend fun getReviewsDeUsuario(id: String): List<ReviewConAlbumDto> {
        return service.getReviewsDeUsuario(id)
    }
}
