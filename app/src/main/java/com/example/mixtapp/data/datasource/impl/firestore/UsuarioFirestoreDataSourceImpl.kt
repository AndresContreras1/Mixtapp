package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.ActualizarUsuarioDto
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsuarioFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : UsuarioRemoteDataSource {

    override suspend fun getUsuarioById(id: String, currentUserId: String): UsuarioPerfilFirestoreDto {
        val docRef = db.collection("usuarios").document(id)
        val respuesta = docRef.get().await()

        // Si el documento no existe, toObject da null: el repositorio lo muestra como no encontrado
        val usuario = respuesta.toObject(UsuarioPerfilFirestoreDto::class.java)?.copy(id = respuesta.id)
            ?: throw NoSuchElementException("No existe el usuario $id")

        // Si en su subcoleccion followers hay un documento con mi uid, ya lo sigo
        if (currentUserId.isNotEmpty()) {
            val followerDoc = docRef.collection("followers").document(currentUserId).get().await()
            usuario.followed = followerDoc.exists()
        }

        return usuario
    }

    override suspend fun getReviewsDeUsuario(id: String, currentUserId: String): List<ReviewConAlbumDto> {
        val snapshot = db.collection("reviews").whereEqualTo("usuarioId", id).get().await()

        return snapshot.documents.map { doc ->
            val review = doc.toObject(ReviewConAlbumDto::class.java)?.copy(id = doc.id)
                ?: throw Exception("Review not found")

            if (currentUserId.isNotEmpty()) {
                val likeSnapshot = doc.reference.collection("likes").document(currentUserId).get().await()
                review.liked = likeSnapshot.exists()
            }

            review
        }
    }

    // El documento se crea con el mismo id del usuario de Authentication
    override suspend fun registrarUsuario(registrarUsuarioDto: RegistrarUsuarioDto, usuarioId: String) {
        val docRef = db.collection("usuarios").document(usuarioId)
        docRef.set(registrarUsuarioDto).await()
    }

    // update solo cambia estos campos: los contadores y la foto se conservan
    override suspend fun actualizarPerfil(usuarioId: String, actualizarUsuarioDto: ActualizarUsuarioDto) {
        val docRef = db.collection("usuarios").document(usuarioId)
        docRef.update(
            mapOf(
                "nombre" to actualizarUsuarioDto.nombre,
                "username" to actualizarUsuarioDto.username,
                "bio" to actualizarUsuarioDto.bio,
            )
        ).await()
    }

    override suspend fun actualizarFotoDePerfil(usuarioId: String, fotoUrl: String) {
        val docRef = db.collection("usuarios").document(usuarioId)
        docRef.update("fotoUrl", fotoUrl).await()
    }

    // El mismo nombre de campo que deja el registro: la Cloud Function lee fcmtoken
    override suspend fun actualizarToken(usuarioId: String, token: String) {
        val docRef = db.collection("usuarios").document(usuarioId)
        docRef.update("fcmtoken", token).await()
    }

    // Seguir son cuatro escrituras en una transaccion: si alguna falla, Firebase deshace las demas.
    // Usuario A sigue a B: following/B en A, followers/A en B, followingCount de A y followersCount de B
    override suspend fun followOrUnfollow(currentUserId: String, targetUserId: String) {
        val currentUserRef = db.collection("usuarios").document(currentUserId)
        val targetUserRef = db.collection("usuarios").document(targetUserId)

        val followingRef = currentUserRef.collection("following").document(targetUserId)
        val followerRef = targetUserRef.collection("followers").document(currentUserId)

        db.runTransaction { transaction ->
            val followingDoc = transaction.get(followingRef)

            if (followingDoc.exists()) {
                // Dejar de seguir
                transaction.delete(followingRef)
                transaction.delete(followerRef)
                transaction.update(currentUserRef, "followingCount", FieldValue.increment(-1))
                transaction.update(targetUserRef, "followersCount", FieldValue.increment(-1))
            } else {
                // Seguir
                transaction.set(followingRef, mapOf("timestamp" to FieldValue.serverTimestamp()))
                transaction.set(followerRef, mapOf("timestamp" to FieldValue.serverTimestamp()))
                transaction.update(currentUserRef, "followingCount", FieldValue.increment(1))
                transaction.update(targetUserRef, "followersCount", FieldValue.increment(1))
            }
        }.await()
    }

    // La lista completa es una segunda consulta: los ids salen de la subcoleccion y los datos de
    // cada usuario de su documento
    override suspend fun getSeguidores(id: String): List<UsuarioPerfilFirestoreDto> =
        usuariosDeLaSubcoleccion(id = id, subcoleccion = "followers")

    override suspend fun getSeguidos(id: String): List<UsuarioPerfilFirestoreDto> =
        usuariosDeLaSubcoleccion(id = id, subcoleccion = "following")

    private suspend fun usuariosDeLaSubcoleccion(id: String, subcoleccion: String): List<UsuarioPerfilFirestoreDto> {
        val snapshot = db.collection("usuarios").document(id).collection(subcoleccion).get().await()

        // Si alguno ya no tiene documento en usuarios, no se muestra
        return snapshot.documents.mapNotNull { doc ->
            val respuesta = db.collection("usuarios").document(doc.id).get().await()
            respuesta.toObject(UsuarioPerfilFirestoreDto::class.java)?.copy(id = respuesta.id)
        }
    }
}
