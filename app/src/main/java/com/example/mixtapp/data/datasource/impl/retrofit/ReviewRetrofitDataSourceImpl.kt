package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.services.AlbumRetrofitService
import com.example.mixtapp.data.datasource.services.ReviewRetrofitService
import com.example.mixtapp.data.dtos.ComentarioDto
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewDto
import com.example.mixtapp.data.dtos.toCreateReviewRetrofitDto
import com.example.mixtapp.data.dtos.toReviewDto
import com.example.mixtapp.data.model.TIPO_ALBUM
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// En el backend solo hay resenas de albumes: las de canciones y artistas quedan como to-do, como
// los likes y los comentarios
class ReviewRetrofitDataSourceImpl @Inject constructor(
    private val service: ReviewRetrofitService,
    private val albumService: AlbumRetrofitService
) : ReviewRemoteDataSource {

    // La resena trae al usuario y al album en el include, en una sola consulta, y se convierte al
    // mismo DTO de Firestore
    override suspend fun getReviewById(id: String, currentUserId: String): ReviewDto {
        return listOf(service.getReviewById(id).toReviewDto()).conMiUsuario(currentUserId).first()
    }

    override suspend fun getReviewsDeItem(tipo: String, itemId: String, currentUserId: String): List<ReviewDto> {
        // to-do: el backend no tiene resenas de canciones ni de artistas
        if (tipo != TIPO_ALBUM) return emptyList()

        return albumService.getReviewsDeAlbum(itemId).map { it.toReviewDto() }.conMiUsuario(currentUserId)
    }

    // En el backend la resena siempre queda a nombre del usuario 1, y solo van los campos de su tabla
    override suspend fun createReview(review: CreateReviewDto) {
        // to-do: el backend no tiene resenas de canciones ni de artistas
        if (review.tipo != TIPO_ALBUM) return

        service.createReview(review.toCreateReviewRetrofitDto(usuarioId = USUARIO_DE_LA_APP_EN_EL_BACKEND))
    }

    override suspend fun updateReview(id: String, review: CreateReviewDto) {
        if (review.tipo != TIPO_ALBUM) return

        service.updateReview(id, review.toCreateReviewRetrofitDto(usuarioId = USUARIO_DE_LA_APP_EN_EL_BACKEND))
    }

    override suspend fun deleteReview(id: String) {
        service.deleteReview(id)
    }

    // to-do: el backend no tiene likes; como en clase, solo funcionan con Firestore
    override suspend fun sendOrDeleteLike(reviewId: String, userId: String) {
    }

    // Con HTTP no hay una conexion continua: flow { emit() } manda una sola vez lo que responde el backend
    override fun listenAllReviews(currentUserId: String): Flow<List<ReviewDto>> = flow {
        emit(service.getReviews().map { it.toReviewDto() }.conMiUsuario(currentUserId))
    }

    override fun listenReviewsDeItem(tipo: String, itemId: String, currentUserId: String): Flow<List<ReviewDto>> = flow {
        emit(getReviewsDeItem(tipo = tipo, itemId = itemId, currentUserId = currentUserId))
    }

    override fun listenReviewById(id: String, currentUserId: String): Flow<ReviewDto> = flow {
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
}
