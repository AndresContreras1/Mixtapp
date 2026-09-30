package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.SocialLocalDataSource
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.ProfileUi
import javax.inject.Inject

class SocialRepository @Inject constructor(
    private val socialLocalDataSource: SocialLocalDataSource
) {

    suspend fun getFollowing(): Result<FollowingUi> {
        return try {
            Result.success(socialLocalDataSource.getFollowing())
        } catch (e: NoSuchElementException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getFriendActivity(): Result<FriendActivityUi> {
        return try {
            Result.success(socialLocalDataSource.getFriendActivity())
        } catch (e: NoSuchElementException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getNotifications(): Result<List<NotificationUi>> {
        return try {
            Result.success(socialLocalDataSource.getNotifications())
        } catch (e: NoSuchElementException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun getProfile(): Result<ProfileUi> {
        return try {
            Result.success(socialLocalDataSource.getProfile())
        } catch (e: NoSuchElementException) {
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    suspend fun darQuitarLikeFollowingReview(reviewId: String): Result<FollowingUi> =
        actualizarFollowingReview(reviewId = reviewId) { review ->
            val nuevoLike = !review.isLiked

            review.copy(
                likes = if (nuevoLike) review.likes + 1 else review.likes - 1,
                isLiked = nuevoLike,
            )
        }

    suspend fun compartirQuitarFollowingReview(reviewId: String): Result<FollowingUi> =
        actualizarFollowingReview(reviewId = reviewId) { it.copy(isShared = !it.isShared) }

    private suspend fun actualizarFollowingReview(
        reviewId: String,
        cambio: (FollowingReviewUi) -> FollowingReviewUi,
    ): Result<FollowingUi> {
        return try {
            val following = socialLocalDataSource.getFollowing()

            if (following.reviews.none { it.id == reviewId }) {
                return Result.failure(ContenidoNoEncontradoException())
            }

            val actualizado = following.copy(
                reviews = following.reviews.map { review ->
                    if (review.id == reviewId) cambio(review) else review
                }
            )

            socialLocalDataSource.guardarFollowing(following = actualizado)
            Result.success(actualizado)
        } catch (e: Exception) {
            Result.failure(ErrorDeDatosLocalesException())
        }
    }
}
