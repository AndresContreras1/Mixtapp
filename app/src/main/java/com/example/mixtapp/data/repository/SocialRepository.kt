package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.SocialLocalDataSource
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.AlbumDeResenaDto
import com.example.mixtapp.data.dtos.toAlbum
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.FollowingStoryUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.NotificationUi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject

class SocialRepository @Inject constructor(
    private val socialLocalDataSource: SocialLocalDataSource,
    private val usuarioRepository: UsuarioRepository,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioFirestoreDataSourceImpl,
    private val reviewRepository: ReviewRepository
) {

    /**
     * Obtiene los datos de la pestaña Seguidos, combinando las reseñas obtenidas de los usuarios
     * a los que sigue el usuario actual en Firebase con las historias (burbujas superiores).
     */
    suspend fun getFollowing(): Result<FollowingUi> {
        return try {
            val currentUserId = authRemoteDataSource.currentUser?.uid
            val localFollowing = socialLocalDataSource.getFollowing()
            val usersResult = usuarioRepository.getUsuariosSeguidos()

            val followedUsers = if (usersResult.isSuccess) {
                usersResult.getOrNull() ?: emptyList()
            } else {
                emptyList()
            }

            val stories = listOf(
                FollowingStoryUi(id = "log", label = "", initials = "+", isAddAction = true, fotoUrl = null)
            ) + followedUsers.map { user ->
                FollowingStoryUi(
                    id = user.id,
                    label = user.nombre.ifEmpty { user.username },
                    initials = user.nombre.take(2).uppercase().ifEmpty { user.username.take(2).uppercase() },
                    isAddAction = false,
                    fotoUrl = user.fotoUrl
                )
            }

            val reviews = coroutineScope {
                followedUsers.map { user ->
                    async {
                        try {
                            val reviewDtos = usuarioRemoteDataSource.getReviewsDeUsuario(user.id)
                            reviewDtos.map { dto ->
                                FollowingReviewUi(
                                    id = dto.id,
                                    reviewerName = user.nombre.ifEmpty { user.username },
                                    reviewerInitials = user.nombre.take(2).uppercase().ifEmpty { user.username.take(2).uppercase() },
                                    reviewedAt = dto.fechaEscucha ?: dto.createdAt ?: "",
                                    album = (dto.album ?: AlbumDeResenaDto()).toAlbum(albumId = dto.albumId),
                                    rating = dto.calificacion,
                                    reviewText = dto.comentario ?: "",
                                    likes = dto.likesDeUsuarios.distinct().size,
                                    comments = 0,
                                    isLiked = currentUserId != null && currentUserId in dto.likesDeUsuarios,
                                    isShared = false,
                                )
                            }
                        } catch (e: Exception) {
                            Log.d("SocialRepository", "getReviewsDeUsuario: ${e.message}")
                            emptyList()
                        }
                    }
                }.awaitAll().flatten().sortedByDescending { it.reviewedAt }
            }

            val following = localFollowing.copy(
                followingCount = followedUsers.size,
                stories = stories,
                reviews = reviews,
            )

            Result.success(following)
        } catch (e: Exception) {
            Log.d("SocialRepository", "getFollowing: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    /**
     * Obtiene la lista de notificaciones sociales.
     */
    suspend fun getNotifications(): Result<List<NotificationUi>> {
        return try {
            Result.success(socialLocalDataSource.getNotifications())
        } catch (e: Exception) {
            Log.d("SocialRepository", "getNotifications: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    /**
     * Da o quita un "me gusta" a una reseña en la sección de seguidos.
     */
    suspend fun darQuitarLikeFollowingReview(reviewId: String): Result<FollowingUi> {
        return try {
            val followingResult = getFollowing()
            val following = followingResult.getOrNull() ?: return followingResult
            val review = following.reviews.find { it.id == reviewId }
                ?: return Result.failure(ContenidoNoEncontradoException())

            val darLike = !review.isLiked
            val likeResult = reviewRepository.actualizarLike(reviewId, darLike)

            if (likeResult.isSuccess) {
                getFollowing()
            } else {
                Result.failure(likeResult.exceptionOrNull() ?: ErrorInesperadoException())
            }
        } catch (e: Exception) {
            Log.d("SocialRepository", "darQuitarLikeFollowingReview: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    /**
     * Marca o desmarca una reseña como compartida en la sección de seguidos.
     */
    suspend fun compartirQuitarFollowingReview(reviewId: String): Result<FollowingUi> =
        actualizarFollowingReview(reviewId = reviewId) { it.copy(isShared = !it.isShared) }

    /**
     * Función auxiliar para actualizar el estado de una reseña de seguidos específica.
     */
    private suspend fun actualizarFollowingReview(
        reviewId: String,
        cambio: (FollowingReviewUi) -> FollowingReviewUi,
    ): Result<FollowingUi> {
        return try {
            val followingResult = getFollowing()
            val following = followingResult.getOrNull() ?: return followingResult

            if (following.reviews.none { it.id == reviewId }) {
                return Result.failure(ContenidoNoEncontradoException())
            }

            val actualizado = following.copy(
                reviews = following.reviews.map { review ->
                    if (review.id == reviewId) cambio(review) else review
                }
            )

            Result.success(actualizado)
        } catch (e: Exception) {
            Log.d("SocialRepository", "actualizarFollowingReview: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }
}
