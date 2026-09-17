package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.ReviewLocalDataSource
import com.example.mixtapp.data.model.DiscussionUi
import com.example.mixtapp.data.model.MyReviewUi
import javax.inject.Inject

class ReviewRepository @Inject constructor(
    private val reviewLocalDataSource: ReviewLocalDataSource
) {

    suspend fun getMyReviews(): Result<List<MyReviewUi>> {
        return try {
            Result.success(reviewLocalDataSource.getMyReviews())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDiscussionByReviewId(reviewId: String): Result<DiscussionUi?> {
        return try {
            Result.success(reviewLocalDataSource.getDiscussionByReviewId(reviewId = reviewId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMoods(): Result<List<String>> {
        return try {
            Result.success(reviewLocalDataSource.getMoods())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFechaEscuchaInicial(): Result<String> {
        return try {
            Result.success(reviewLocalDataSource.getFechaEscuchaInicial())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFechaEscuchaSugerida(): Result<String> {
        return try {
            Result.success(reviewLocalDataSource.getFechaEscuchaSugerida())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
