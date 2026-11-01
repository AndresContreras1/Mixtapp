package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
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

    override suspend fun getAlbumById(id: String, currentUserId: String): AlbumDto? {
        val albumRef = db.collection("albumes").document(id)
        val respuesta = albumRef.get().await()
        // Si el documento no existe, toObject da null y se retorna null: el repositorio decide el error
        val album = respuesta.toObject(AlbumDto::class.java)?.copy(id = respuesta.id) ?: return null

        album.liked = albumRef.tieneMiLike(currentUserId)
        return album
    }

    // El listener escucha la coleccion: un like cambia likesCount de un album y llega la lista
    // completa por el flujo
    override fun listenAlbumes(): Flow<List<AlbumDto>> = callbackFlow {
        val listenerRegistration = db.collection("albumes")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val albumes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(AlbumDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(albumes).isSuccess // envia la lista al flujo
                }
            }

        // Cuando nadie escucha el flujo, se quita el listener para no dejar la conexion activa
        awaitClose { listenerRegistration.remove() }
    }

    // El listener escucha el documento: mi like cambia likesCount, asi que tambien llega por aqui y
    // se vuelve a buscar con exists(). Si no existe se envia null
    override fun listenAlbumById(id: String, currentUserId: String): Flow<AlbumDto?> = callbackFlow {
        val listenerRegistration = db.collection("albumes").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val album = snapshot.toObject(AlbumDto::class.java)?.copy(id = snapshot.id)
                    trySend(album).isSuccess // envia el album (o null) al flujo
                }
            }

        // Cuando se sale del detalle nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }.map { album ->
        if (album != null) {
            album.liked = db.collection("albumes").document(album.id).tieneMiLike(currentUserId)
        }

        album
    }

    override suspend fun sendOrDeleteLike(albumId: String, userId: String, copia: CopiaDeItemDto) {
        db.sendOrDeleteLikeDeItem(coleccion = "albumes", itemId = albumId, userId = userId, copia = copia)
    }
}
