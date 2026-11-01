package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.ActualizarUsuarioDto
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsuarioFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : UsuarioRemoteDataSource {

    override suspend fun getUsuarioById(id: String, currentUserId: String): UsuarioPerfilFirestoreDto? {
        val docRef = db.collection("usuarios").document(id)
        val respuesta = docRef.get().await()

        // Si el documento no existe, toObject da null y se retorna null: el repositorio decide el error
        val usuario = respuesta.toObject(UsuarioPerfilFirestoreDto::class.java)?.copy(id = respuesta.id)
            ?: return null

        usuario.followed = loSigo(usuarioId = id, currentUserId = currentUserId)
        return usuario
    }

    // Las resenas que escribio un usuario, con mi like en cada una
    override suspend fun getReviewsDeUsuario(id: String, currentUserId: String): List<ReviewConAlbumDto> {
        val snapshot = db.collection("reviews").whereEqualTo("usuarioId", id).get().await()
        val reviews = snapshot.documents.mapNotNull { doc ->
            doc.toObject(ReviewConAlbumDto::class.java)?.copy(id = doc.id)
        }

        return conMisLikes(reviews = reviews, currentUserId = currentUserId)
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

    // La URL de la foto que quedo en Storage; ese cambio es el que escucha la Cloud Function que
    // pone la foto nueva en las resenas y los comentarios
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
    override suspend fun getSeguidores(id: String): List<UsuarioPerfilFirestoreDto> {
        val snapshot = db.collection("usuarios").document(id).collection("followers").get().await()
        return usuariosConIds(snapshot.documents.map { doc -> doc.id })
    }

    override suspend fun getSeguidos(id: String): List<UsuarioPerfilFirestoreDto> {
        val snapshot = db.collection("usuarios").document(id).collection("following").get().await()
        return usuariosConIds(snapshot.documents.map { doc -> doc.id })
    }

    // El listener escucha el documento del usuario: seguir o dejar de seguir cambia sus contadores,
    // asi que tambien llega por aqui. Si el documento no existe se envia null y el repositorio
    // decide el error. Si lo sigo se busca con exists() cada vez que llega
    override fun listenUsuarioById(id: String, currentUserId: String): Flow<UsuarioPerfilFirestoreDto?> = callbackFlow {
        val listenerRegistration = db.collection("usuarios").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val usuario = snapshot.toObject(UsuarioPerfilFirestoreDto::class.java)?.copy(id = snapshot.id)
                    trySend(usuario).isSuccess // envia el usuario (o null) al flujo
                }
            }

        // Cuando se sale del perfil nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }.map { usuario ->
        if (usuario != null) {
            usuario.followed = loSigo(usuarioId = usuario.id, currentUserId = currentUserId)
        }

        usuario
    }

    // Un like en otro celular cambia likesCount en la resena, asi que la lista llega de nuevo
    override fun listenReviewsDeUsuario(id: String, currentUserId: String): Flow<List<ReviewConAlbumDto>> = callbackFlow {
        val listenerRegistration = db.collection("reviews").whereEqualTo("usuarioId", id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reviews = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ReviewConAlbumDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(reviews).isSuccess // envia la lista al flujo
                }
            }

        awaitClose { listenerRegistration.remove() }
    }.map { reviews -> conMisLikes(reviews = reviews, currentUserId = currentUserId) }

    override fun listenSeguidores(id: String): Flow<List<UsuarioPerfilFirestoreDto>> =
        listenSubcoleccion(id = id, subcoleccion = "followers")

    override fun listenSeguidos(id: String): Flow<List<UsuarioPerfilFirestoreDto>> =
        listenSubcoleccion(id = id, subcoleccion = "following")

    // El listener escucha la subcoleccion: llegan los ids y los datos de cada usuario salen de su
    // documento, como en la lista sin vivo
    private fun listenSubcoleccion(id: String, subcoleccion: String): Flow<List<UsuarioPerfilFirestoreDto>> = callbackFlow {
        val listenerRegistration = db.collection("usuarios").document(id).collection(subcoleccion)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    trySend(snapshot.documents.map { doc -> doc.id }).isSuccess // envia los ids al flujo
                }
            }

        awaitClose { listenerRegistration.remove() }
    }.map { ids -> usuariosConIds(ids) }

    // Los documentos de varios usuarios se piden a la vez y se espera el mas lento. Si alguno ya
    // no tiene documento en usuarios, no se muestra
    private suspend fun usuariosConIds(ids: List<String>): List<UsuarioPerfilFirestoreDto> = coroutineScope {
        val pendientes = ids.map { usuarioId ->
            async {
                val respuesta = db.collection("usuarios").document(usuarioId).get().await()
                respuesta.toObject(UsuarioPerfilFirestoreDto::class.java)?.copy(id = respuesta.id)
            }
        }

        pendientes.mapNotNull { it.await() }
    }

    // Si en su subcoleccion followers hay un documento con mi uid, ya lo sigo
    private suspend fun loSigo(usuarioId: String, currentUserId: String): Boolean {
        if (currentUserId.isEmpty()) return false

        return db.collection("usuarios").document(usuarioId)
            .collection("followers").document(currentUserId).get().await().exists()
    }

    // Mi like de cada resena se pide a la vez y se espera el mas lento, en vez de uno por uno
    private suspend fun conMisLikes(
        reviews: List<ReviewConAlbumDto>,
        currentUserId: String,
    ): List<ReviewConAlbumDto> = coroutineScope {
        val pendientes = reviews.map { review ->
            async {
                review.liked = db.collection("reviews").document(review.id).tieneMiLike(currentUserId)
                review
            }
        }

        pendientes.map { it.await() }
    }
}
