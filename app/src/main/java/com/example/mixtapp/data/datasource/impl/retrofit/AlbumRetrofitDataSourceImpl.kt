package com.example.mixtapp.data.datasource.impl

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

    override suspend fun getReviewsDeAlbum(id: String): List<ReviewConUsuarioDto> {
        return service.getReviewsDeAlbum(id)
    }
}
