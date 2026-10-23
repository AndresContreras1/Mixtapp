package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalDiscussionProvider
import com.example.mixtapp.data.model.DiscussionCommentUi
import javax.inject.Inject

// Los comentarios de cada discusion siguen locales. Las funciones son suspend como las remotas:
// si estos datos pasan a la base remota, el ViewModel no cambia
class ReviewLocalDataSource @Inject constructor() {

    suspend fun getComentarios(reviewId: String): List<DiscussionCommentUi> =
        LocalDiscussionProvider.comentarios[reviewId] ?: emptyList()

    suspend fun guardarComentarios(reviewId: String, comentarios: List<DiscussionCommentUi>): Unit {
        LocalDiscussionProvider.comentarios[reviewId] = comentarios
    }
}
