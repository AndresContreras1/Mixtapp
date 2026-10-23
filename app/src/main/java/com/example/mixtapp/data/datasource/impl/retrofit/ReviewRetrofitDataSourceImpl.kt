package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.services.AlbumRetrofitService
import com.example.mixtapp.data.datasource.services.ReviewRetrofitService
import com.example.mixtapp.data.dtos.AlbumDeResenaDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ReviewRetrofitDataSourceImpl @Inject constructor(
    private val service: ReviewRetrofitService,
    private val albumService: AlbumRetrofitService
) : ReviewRemoteDataSource {

    // El backend no tiene likes. La resena trae al usuario en el include, pero no copia el album
    // como Firestore: se pide aparte para que el DTO llegue igual por las dos bases
    override suspend fun getReviewById(id: String, currentUserId: String): ReviewConAlbumDto {
        val review = service.getReviewById(id)
        val album = albumService.getAlbumById(review.albumId)

        return review.copy(
            album = AlbumDeResenaDto(
                id = album.id,
                titulo = album.titulo,
                artista = album.artista,
                portadaUrl = album.portadaUrl,
            )
        )
    }

    // En el backend la resena siempre queda a nombre del usuario 1, y solo van los campos de su
    // tabla: las copias del usuario y del album son de Firestore
    override suspend fun createReview(review: CreateReviewDto) {
        service.createReview(paraElBackend(review))
    }

    override suspend fun updateReview(id: String, review: CreateReviewDto) {
        service.updateReview(id, paraElBackend(review))
    }

    override suspend fun deleteReview(id: String) {
        service.deleteReview(id)
    }

    // to-do: el backend no tiene likes; como en clase, solo funcionan con Firestore
    override suspend fun sendOrDeleteLike(reviewId: String, userId: String) {
    }

    // Con HTTP no hay una conexion continua: el flujo manda una sola vez lo que responde el backend
    override fun listenReviewById(id: String, currentUserId: String): Flow<ReviewConAlbumDto> = callbackFlow {
        trySend(getReviewById(id = id, currentUserId = currentUserId)).isSuccess

        awaitClose { }
    }

    private fun paraElBackend(review: CreateReviewDto): CreateReviewDto =
        review.copy(usuarioId = USUARIO_DE_LA_APP_EN_EL_BACKEND, usuario = null, album = null)
}
