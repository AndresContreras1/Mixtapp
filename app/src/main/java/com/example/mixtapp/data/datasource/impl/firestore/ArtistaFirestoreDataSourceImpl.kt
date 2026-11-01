package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.ArtistaRemoteDataSource
import com.example.mixtapp.data.dtos.ArtistaDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Igual que los albumes, con la coleccion artistas
class ArtistaFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : ArtistaRemoteDataSource {

    override suspend fun getArtistas(): List<ArtistaDto> {
        val snapshot = db.collection("artistas").get().await()

        return snapshot.documents.mapNotNull { doc ->
            doc.toObject(ArtistaDto::class.java)?.copy(id = doc.id)
        }
    }

    override suspend fun getArtistaById(id: String, currentUserId: String): ArtistaDto? {
        val artistaRef = db.collection("artistas").document(id)
        val respuesta = artistaRef.get().await()
        // Si el documento no existe se retorna null: el repositorio decide el error
        val artista = respuesta.toObject(ArtistaDto::class.java)?.copy(id = respuesta.id) ?: return null

        artista.liked = artistaRef.tieneMiLike(currentUserId)
        return artista
    }

    override fun listenArtistas(): Flow<List<ArtistaDto>> = callbackFlow {
        val listenerRegistration = db.collection("artistas")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val artistas = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ArtistaDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(artistas).isSuccess // envia la lista al flujo
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

    override fun listenArtistaById(id: String, currentUserId: String): Flow<ArtistaDto?> = callbackFlow {
        val listenerRegistration = db.collection("artistas").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val artista = snapshot.toObject(ArtistaDto::class.java)?.copy(id = snapshot.id)
                    trySend(artista).isSuccess // envia el artista (o null) al flujo
                }
            }

        awaitClose { listenerRegistration.remove() }
    }.map { artista ->
        if (artista != null) {
            artista.liked = db.collection("artistas").document(artista.id).tieneMiLike(currentUserId)
        }

        artista
    }

    override suspend fun sendOrDeleteLike(artistaId: String, userId: String, copia: CopiaDeItemDto) {
        db.sendOrDeleteLikeDeItem(coleccion = "artistas", itemId = artistaId, userId = userId, copia = copia)
    }
}
