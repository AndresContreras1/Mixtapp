package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.SocialLocalDataSource
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
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFriendActivity(): Result<FriendActivityUi> {
        return try {
            Result.success(socialLocalDataSource.getFriendActivity())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNotifications(): Result<List<NotificationUi>> {
        return try {
            Result.success(socialLocalDataSource.getNotifications())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProfile(): Result<ProfileUi> {
        return try {
            Result.success(socialLocalDataSource.getProfile())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun darQuitarLikeFollowingReview(reviewId: String): Result<FollowingUi> {
        return try {
            Result.success(socialLocalDataSource.darQuitarLikeFollowingReview(reviewId = reviewId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun compartirQuitarFollowingReview(reviewId: String): Result<FollowingUi> {
        return try {
            Result.success(socialLocalDataSource.compartirQuitarFollowingReview(reviewId = reviewId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
