package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import kotlinx.coroutines.flow.Flow

interface AlbumRemoteDataSource {
    suspend fun getAlbumes(): List<AlbumDto>
    suspend fun getAlbumById(id: String): AlbumDto
    // Con el usuario de la app se sabe a cuales resenas ya les dio like. Es opcional para que
    // Retrofit no tenga que usarlo
    suspend fun getReviewsDeAlbum(id: String, currentUserId: String = ""): List<ReviewConUsuarioDto>
    // Datos en vivo: cada vez que cambia una resena llega la lista completa de nuevo
    fun listenAllReviews(): Flow<List<ReviewConUsuarioDto>>
}
