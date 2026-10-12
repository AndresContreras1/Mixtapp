package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.services.AlbumRetrofitService
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import javax.inject.Inject

class AlbumRetrofitDataSourceImpl @Inject constructor(
    private val service: AlbumRetrofitService,
    private val authRemoteDataSource: AuthRemoteDataSource
) : AlbumRemoteDataSource {

    override suspend fun getAlbumes(): List<AlbumDto> {
        return service.getAlbumes()
    }

    override suspend fun getAlbumById(id: String): AlbumDto {
        return service.getAlbumById(id)
    }

    // El usuario 1 del backend es el usuario de la app: sus resenas vuelven con su uid,
    // igual que en Firestore, para que el repositorio las reconozca como mias
    override suspend fun getReviewsDeAlbum(id: String): List<ReviewConUsuarioDto> {
        val uid = authRemoteDataSource.currentUser?.uid ?: USUARIO_DE_LA_APP_EN_EL_BACKEND

        return service.getReviewsDeAlbum(id).map { review ->
            if (review.usuarioId == USUARIO_DE_LA_APP_EN_EL_BACKEND) review.copy(usuarioId = uid) else review
        }
    }
}
