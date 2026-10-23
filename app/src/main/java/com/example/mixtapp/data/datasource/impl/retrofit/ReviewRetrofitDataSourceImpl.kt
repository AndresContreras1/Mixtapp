package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.services.ReviewRetrofitService
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import javax.inject.Inject

class ReviewRetrofitDataSourceImpl @Inject constructor(
    private val service: ReviewRetrofitService
) : ReviewRemoteDataSource {

    // to-do: el backend no tiene una ruta para traer una resena por su id. Se avisa como no
    // encontrada, igual que un documento que no existe en Firestore
    override suspend fun getReviewById(id: String, currentUserId: String): ReviewConAlbumDto {
        throw NoSuchElementException("El backend no tiene la resena $id")
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

    private fun paraElBackend(review: CreateReviewDto): CreateReviewDto =
        review.copy(usuarioId = USUARIO_DE_LA_APP_EN_EL_BACKEND, usuario = null, album = null)
}
