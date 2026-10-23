package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.services.AlbumRetrofitService
import com.example.mixtapp.data.datasource.services.ReviewRetrofitService
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class AlbumRetrofitDataSourceImpl @Inject constructor(
    private val service: AlbumRetrofitService,
    private val reviewService: ReviewRetrofitService
) : AlbumRemoteDataSource {

    override suspend fun getAlbumes(): List<AlbumDto> {
        return service.getAlbumes()
    }

    override suspend fun getAlbumById(id: String): AlbumDto {
        return service.getAlbumById(id)
    }

    // El backend no tiene likes
    override suspend fun getReviewsDeAlbum(id: String, currentUserId: String): List<ReviewConUsuarioDto> {
        return conMiUsuario(reviews = service.getReviewsDeAlbum(id), currentUserId = currentUserId)
    }

    // Con HTTP no hay una conexion continua: el flujo manda una sola vez lo que responde el backend
    override fun listenAllReviews(currentUserId: String): Flow<List<ReviewConUsuarioDto>> = callbackFlow {
        val reviews = reviewService.getReviews()
        trySend(conMiUsuario(reviews = reviews, currentUserId = currentUserId)).isSuccess

        awaitClose { }
    }

    // En el backend el usuario de la app es el 1: sus resenas vuelven con el uid de la sesion para
    // que el repositorio las reconozca como mias
    private fun conMiUsuario(reviews: List<ReviewConUsuarioDto>, currentUserId: String): List<ReviewConUsuarioDto> =
        reviews.map { review ->
            if (currentUserId.isNotEmpty() && review.usuarioId == USUARIO_DE_LA_APP_EN_EL_BACKEND) {
                review.copy(usuarioId = currentUserId)
            } else {
                review
            }
        }
}
