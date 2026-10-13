package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.services.ReviewRetrofitService
import com.example.mixtapp.data.dtos.CreateReviewDto
import javax.inject.Inject

class ReviewRetrofitDataSourceImpl @Inject constructor(
    private val service: ReviewRetrofitService
) : ReviewRemoteDataSource {

    // En el backend la resena siempre queda a nombre del usuario 1, y solo van los campos de su
    // tabla: las copias del usuario y del album son de Firestore
    override suspend fun createReview(review: CreateReviewDto) {
        service.createReview(paraElBackend(review))
    }

    override suspend fun updateReview(id: String, review: CreateReviewDto) {
        service.updateReview(id, paraElBackend(review))
    }

    override suspend fun deleteReview(id: String): Unit? {
        return service.deleteReview(id)
    }

    private fun paraElBackend(review: CreateReviewDto): CreateReviewDto =
        review.copy(usuarioId = USUARIO_DE_LA_APP_EN_EL_BACKEND, usuario = null, album = null)
}
