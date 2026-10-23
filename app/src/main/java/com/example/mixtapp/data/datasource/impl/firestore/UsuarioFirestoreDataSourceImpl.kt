package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsuarioFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : UsuarioRemoteDataSource {

    // La transaccion conserva los cambios de otros usuarios y no duplica la relacion.
    suspend fun actualizarSeguimiento(id: String, usuarioId: String, activo: Boolean): List<String> {
        val referencia = db.collection("usuarios").document(id)
        return db.runTransaction { transaccion ->
            val documento = transaccion.get(referencia)
            if (!documento.exists()) throw NoSuchElementException("No existe el documento $id")
            val actuales = (documento.get("seguidoresIds") as? List<*>)
                ?.filterIsInstance<String>() ?: emptyList()
            val nuevos = if (activo) (actuales + usuarioId).distinct() else actuales.filter { it != usuarioId }
            transaccion.update(referencia, "seguidoresIds", nuevos)
            nuevos
        }.await()
    }

    override suspend fun getUsuarioById(id: String): UsuarioPerfilFirestoreDto {
        val docRef = db.collection("usuarios").document(id)
        val respuesta = docRef.get().await()
        val usuario = respuesta.toObject(UsuarioPerfilFirestoreDto::class.java)

        // Si el documento no existe, toObject da null: el repositorio lo muestra como no encontrado
        return usuario?.copy(id = respuesta.id) ?: throw NoSuchElementException("No existe el usuario $id")
    }

    // Obtiene una lista con todos los documentos de la colección 'usuarios' en Firestore.
    override suspend fun getUsuarios(): List<UsuarioPerfilFirestoreDto> {
        val snapshot = db.collection("usuarios").get().await()
        return snapshot.documents.map { doc ->
            val usuario = doc.toObject(UsuarioPerfilFirestoreDto::class.java)
            usuario?.copy(id = doc.id) ?: throw Exception("Usuario not found")
        }
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
        docRef.set(registrarUsuarioDto, SetOptions.merge()).await()
    }

    // En Firestore el documento del usuario tiene el mismo uid de Authentication
    override fun idDelUsuarioEnLaBase(uid: String): String = uid
}
