package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto

interface AlbumRemoteDataSource {
    suspend fun getAlbumes(): List<AlbumDto>
    suspend fun getAlbumById(id: String): AlbumDto
    suspend fun getReviewsDeAlbum(id: String): List<ReviewConUsuarioDto>
}
