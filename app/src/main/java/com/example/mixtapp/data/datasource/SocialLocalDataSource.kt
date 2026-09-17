package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalFollowingProvider
import com.example.mixtapp.data.local.LocalFriendActivityProvider
import com.example.mixtapp.data.local.LocalNotificationsProvider
import com.example.mixtapp.data.local.LocalProfileProvider
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.ProfileUi
import javax.inject.Inject

class SocialLocalDataSource @Inject constructor() {

    suspend fun getFollowing(): FollowingUi = LocalFollowingProvider.following

    suspend fun getFriendActivity(): FriendActivityUi = LocalFriendActivityProvider.friendActivity

    suspend fun getNotifications(): List<NotificationUi> = LocalNotificationsProvider.notifications

    suspend fun getProfile(): ProfileUi = LocalProfileProvider.profile

    suspend fun darQuitarLikeFollowingReview(reviewId: String): FollowingUi =
        actualizarFollowingReview(reviewId = reviewId) { it.copy(isLiked = !it.isLiked) }

    suspend fun compartirQuitarFollowingReview(reviewId: String): FollowingUi =
        actualizarFollowingReview(reviewId = reviewId) { it.copy(isShared = !it.isShared) }

    private fun actualizarFollowingReview(
        reviewId: String,
        cambio: (FollowingReviewUi) -> FollowingReviewUi,
    ): FollowingUi {
        val following = LocalFollowingProvider.following
        val actualizado = following.copy(
            reviews = following.reviews.map { review ->
                if (review.id == reviewId) cambio(review) else review
            }
        )
        LocalFollowingProvider.following = actualizado
        return actualizado
    }
}
