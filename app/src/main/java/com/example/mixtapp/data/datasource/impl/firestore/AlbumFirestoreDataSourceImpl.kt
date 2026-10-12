package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AlbumFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : AlbumRemoteDataSource {

    override suspend fun getAlbumes(): List<AlbumDto> {
        val snapshot = db.collection("albumes").get().await()

        // El id no viene dentro de los campos: se copia el del documento para poder navegar
        return snapshot.documents.map { doc ->
            val album = doc.toObject(AlbumDto::class.java)
            album?.copy(id = doc.id) ?: throw Exception("Album not found")
        }
    }

    override suspend fun getAlbumById(id: String): AlbumDto {
        val respuesta = db.collection("albumes").document(id).get().await()
        val album = respuesta.toObject(AlbumDto::class.java)

        // Si el documento no existe, toObject da null: el repositorio lo muestra como no encontrado
        return album?.copy(id = respuesta.id) ?: throw NoSuchElementException("No existe el album $id")
    }

    override suspend fun getReviewsDeAlbum(id: String): List<ReviewConUsuarioDto> {
        val snapshot = db.collection("reviews").whereEqualTo("albumId", id).get().await()

        return snapshot.documents.map { doc ->
            val review = doc.toObject(ReviewConUsuarioDto::class.java)
            review?.copy(id = doc.id) ?: throw Exception("Review not found")
        }
    }
}
