package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.services.AlbumRetrofitService
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import javax.inject.Inject

class AlbumRetrofitDataSourceImpl @Inject constructor(
    private val service: AlbumRetrofitService
) : AlbumRemoteDataSource {

    override suspend fun getAlbumes(): List<AlbumDto> {
        return service.getAlbumes()
    }

    override suspend fun getAlbumById(id: String): AlbumDto {
        return service.getAlbumById(id)
    }

    // El backend no tiene likes. En el backend el usuario de la app es el 1: sus resenas vuelven
    // con el uid de la sesion para que el repositorio las reconozca como mias
    override suspend fun getReviewsDeAlbum(id: String, currentUserId: String): List<ReviewConUsuarioDto> {
        return service.getReviewsDeAlbum(id).map { review ->
            if (currentUserId.isNotEmpty() && review.usuarioId == USUARIO_DE_LA_APP_EN_EL_BACKEND) {
                review.copy(usuarioId = currentUserId)
            } else {
                review
            }
        }
    }
}
