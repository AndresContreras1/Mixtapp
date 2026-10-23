package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.services.AlbumRetrofitService
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    // Con HTTP no hay una conexion continua: el flujo manda una sola vez lo que responde el backend
    override fun listenAllReviews(): Flow<List<ReviewConUsuarioDto>> = callbackFlow {
        val reviews = service.getAlbumes().flatMap { album -> service.getReviewsDeAlbum(album.id) }
        trySend(reviews).isSuccess

        awaitClose { }
    }
}
