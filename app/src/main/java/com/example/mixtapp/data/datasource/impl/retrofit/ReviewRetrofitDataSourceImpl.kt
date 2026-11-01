package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.services.ReviewRetrofitService
import com.example.mixtapp.data.dtos.ComentarioDto
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ReviewRetrofitDataSourceImpl @Inject constructor(
    private val service: ReviewRetrofitService
) : ReviewRemoteDataSource {

    // El backend no tiene likes. La resena trae al usuario y al album en el include, en una sola
    // consulta, asi que el DTO llega igual que desde Firestore
    override suspend fun getReviewById(id: String, currentUserId: String): ReviewConAlbumDto {
        return service.getReviewById(id)
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

    // Con HTTP no hay una conexion continua: flow { emit() } manda una sola vez lo que responde el backend
    override fun listenReviewById(id: String, currentUserId: String): Flow<ReviewConAlbumDto> = flow {
        emit(getReviewById(id = id, currentUserId = currentUserId))
    }

    // to-do: el backend no tiene comentarios; como los likes, solo funcionan con Firestore
    override suspend fun createComentario(reviewId: String, comentario: CreateComentarioDto) {
    }

    override suspend fun sendOrDeleteLikeComentario(reviewId: String, comentarioId: String, userId: String) {
    }

    // El backend no tiene comentarios: la lista llega vacia
    override fun listenComentarios(reviewId: String, currentUserId: String): Flow<List<ComentarioDto>> = flow {
        emit(emptyList<ComentarioDto>())
    }

    private fun paraElBackend(review: CreateReviewDto): CreateReviewDto =
        review.copy(usuarioId = USUARIO_DE_LA_APP_EN_EL_BACKEND, usuario = null, album = null)
}
