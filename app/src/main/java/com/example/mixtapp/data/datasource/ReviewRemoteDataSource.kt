package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.ComentarioDto
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import kotlinx.coroutines.flow.Flow

interface ReviewRemoteDataSource {
    // Si no existe retorna null: el repositorio decide el error
    suspend fun getReviewById(id: String, currentUserId: String = ""): ReviewConAlbumDto?
    suspend fun createReview(review: CreateReviewDto): Unit
    suspend fun updateReview(id: String, review: CreateReviewDto): Unit
    suspend fun deleteReview(id: String): Unit
    // Una sola funcion da el like o lo quita, segun si ya existe
    suspend fun sendOrDeleteLike(reviewId: String, userId: String): Unit
    // La resena en vivo, con mi like: un like en otro celular cambia likesCount y llega de nuevo.
    // Si no existe (o la borran mientras se ve) llega null: el repositorio decide el error
    fun listenReviewById(id: String, currentUserId: String = ""): Flow<ReviewConAlbumDto?>
    // Los comentarios de la discusion: crear uno sube commentsCount de la resena
    suspend fun createComentario(reviewId: String, comentario: CreateComentarioDto): Unit
    // Igual que en la resena, una sola funcion da el like al comentario o lo quita
    suspend fun sendOrDeleteLikeComentario(reviewId: String, comentarioId: String, userId: String): Unit
    // Los comentarios en vivo, con mis likes: es como un chat, cada comentario nuevo llega solo
    fun listenComentarios(reviewId: String, currentUserId: String = ""): Flow<List<ComentarioDto>>
}
