package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.services.AlbumRetrofitService
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AlbumRetrofitDataSourceImpl @Inject constructor(
    private val service: AlbumRetrofitService
) : AlbumRemoteDataSource {

    override suspend fun getAlbumes(): List<AlbumDto> {
        return service.getAlbumes()
    }

    // El backend no tiene likes: el corazon queda vacio
    override suspend fun getAlbumById(id: String, currentUserId: String): AlbumDto {
        return service.getAlbumById(id)
    }

    // Con HTTP no hay una conexion continua: flow { emit() } manda una sola vez lo que responde el backend
    override fun listenAlbumes(): Flow<List<AlbumDto>> = flow {
        emit(getAlbumes())
    }

    override fun listenAlbumById(id: String, currentUserId: String): Flow<AlbumDto> = flow {
        emit(getAlbumById(id = id, currentUserId = currentUserId))
    }

    // to-do: el backend no tiene likes; como en clase, solo funcionan con Firestore
    override suspend fun sendOrDeleteLike(albumId: String, userId: String, copia: CopiaDeItemDto) {
    }
}
