package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReviewFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ReviewRemoteDataSource {

    // add crea el documento con un id aleatorio
    override suspend fun createReview(review: CreateReviewDto) {
        db.collection("reviews").add(review).await()
    }

    // set sobre el documento que ya existe lo reemplaza con los datos nuevos
    override suspend fun updateReview(id: String, review: CreateReviewDto) {
        db.collection("reviews").document(id).set(review).await()
    }

    override suspend fun deleteReview(id: String): Unit {
        db.collection("reviews").document(id).delete().await()
    }
}
