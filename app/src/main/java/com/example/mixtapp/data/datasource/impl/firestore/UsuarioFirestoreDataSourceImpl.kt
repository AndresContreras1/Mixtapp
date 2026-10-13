package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsuarioFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : UsuarioRemoteDataSource {

    override suspend fun getUsuarioById(id: String): UsuarioPerfilFirestoreDto {
        val docRef = db.collection("usuarios").document(id)
        val respuesta = docRef.get().await()
        val usuario = respuesta.toObject(UsuarioPerfilFirestoreDto::class.java)

        // Si el documento no existe, toObject da null: el repositorio lo muestra como no encontrado
        return usuario?.copy(id = respuesta.id) ?: throw NoSuchElementException("No existe el usuario $id")
    }

    override suspend fun getReviewsDeUsuario(id: String): List<ReviewConAlbumDto> {
        val snapshot = db.collection("reviews").whereEqualTo("usuarioId", id).get().await()

        return snapshot.documents.map { doc ->
            val review = doc.toObject(ReviewConAlbumDto::class.java)
            review?.copy(id = doc.id) ?: throw Exception("Review not found")
        }
    }

    // El documento se crea con el mismo id del usuario de Authentication
    override suspend fun registrarUsuario(registrarUsuarioDto: RegistrarUsuarioDto, usuarioId: String) {
        val docRef = db.collection("usuarios").document(usuarioId)
        docRef.set(registrarUsuarioDto).await()
    }

    // En Firestore el documento del usuario tiene el mismo uid de Authentication
    override fun idDelUsuarioEnLaBase(uid: String): String = uid
}
