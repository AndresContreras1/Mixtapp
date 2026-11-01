package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AlbumFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : AlbumRemoteDataSource {

    override suspend fun getAlbumes(): List<AlbumDto> {
        val snapshot = db.collection("albumes").get().await()

        // El id no viene dentro de los campos: se copia el del documento para poder navegar. Un
        // documento que no se puede leer no se muestra
        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(AlbumDto::class.java)?.copy(id = doc.id)
        }
    }

    override suspend fun getAlbumById(id: String): AlbumDto? {
        val respuesta = db.collection("albumes").document(id).get().await()
        // Si el documento no existe, toObject da null y se retorna null: el repositorio decide el error
        val album = respuesta.toObject(AlbumDto::class.java) ?: return null

        return album.copy(id = respuesta.id)
    }

    override suspend fun getReviewsDeAlbum(id: String, currentUserId: String): List<ReviewConUsuarioDto> {
        val snapshot = db.collection("reviews").whereEqualTo("albumId", id).get().await()
        val reviews = snapshot.documents.mapNotNull { doc ->
            doc.toObject(ReviewConUsuarioDto::class.java)?.copy(id = doc.id)
        }

        return conMisLikes(reviews = reviews, currentUserId = currentUserId)
    }

    // El listener escucha la coleccion reviews: cada cambio manda la lista completa por el flujo.
    // Un like solo cambia likesCount en la resena, asi que tambien llega por aqui. El inicio no
    // pinta corazones: mi like solo se busca si llega el usuario
    override fun listenAllReviews(currentUserId: String): Flow<List<ReviewConUsuarioDto>> = callbackFlow {
        val listenerRegistration = db.collection("reviews")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reviews = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ReviewConUsuarioDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(reviews).isSuccess // envia la lista al flujo
                }
            }

        // Cuando nadie escucha el flujo, se quita el listener para no dejar la conexion activa
        awaitClose { listenerRegistration.remove() }
    }.map { reviews -> conMisLikes(reviews = reviews, currentUserId = currentUserId) }

    // Escucha solo las resenas de este album. El listener no puede esperar otra consulta, asi que
    // mi like de cada resena se busca con exists() cada vez que llega la lista
    override fun listenReviewsDeAlbum(id: String, currentUserId: String): Flow<List<ReviewConUsuarioDto>> = callbackFlow {
        val listenerRegistration = db.collection("reviews").whereEqualTo("albumId", id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val reviews = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ReviewConUsuarioDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(reviews).isSuccess // envia la lista al flujo
                }
            }

        // Cuando se sale del album nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }.map { reviews -> conMisLikes(reviews = reviews, currentUserId = currentUserId) }

    // Mi like de cada resena se pide a la vez y se espera el mas lento, en vez de uno por uno
    private suspend fun conMisLikes(
        reviews: List<ReviewConUsuarioDto>,
        currentUserId: String,
    ): List<ReviewConUsuarioDto> = coroutineScope {
        val pendientes = reviews.map { review ->
            async {
                review.liked = db.collection("reviews").document(review.id).tieneMiLike(currentUserId)
                review
            }
        }

        pendientes.map { it.await() }
    }
}
