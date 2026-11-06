package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.time.Instant
import javax.inject.Inject

class MixtapeFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : MixtapeRemoteDataSource {

    override fun escucharMixtapesDelUsuario(
        usuarioId: String,
        currentUserId: String?
    ): Flow<List<MixtapeDto>> = callbackFlow {
        val listener = db.collection("mixtapes")
            .whereEqualTo("usuarioId", usuarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val mixtapes = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(MixtapeDto::class.java)?.copy(id = doc.id)
                }?.sortedByDescending { it.createdAt } ?: emptyList()

                trySend(mixtapes)
            }

        awaitClose { listener.remove() }
    }

    override fun escucharMixtapePorId(
        id: String,
        currentUserId: String?
    ): Flow<MixtapeDto?> = callbackFlow {
        val listener = db.collection("mixtapes").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val mixtape = snapshot?.toObject(MixtapeDto::class.java)?.copy(id = snapshot.id)
                trySend(mixtape)
            }

        awaitClose { listener.remove() }
    }

    override suspend fun crearMixtape(
        nombre: String,
        descripcion: String?,
        usuarioId: String,
        usuarioNombre: String,
        usuarioFotoUrl: String?,
        primerItem: ItemMixtapeDto?
    ): String {
        val docRef = db.collection("mixtapes").document()
        val items = if (primerItem != null) listOf(primerItem) else emptyList()
        val mixtape = MixtapeDto(
            id = docRef.id,
            nombre = nombre,
            descripcion = descripcion,
            usuarioId = usuarioId,
            usuarioNombre = usuarioNombre,
            usuarioFotoUrl = usuarioFotoUrl,
            items = items,
            likesCount = 0,
            createdAt = Instant.now().toString()
        )
        docRef.set(mixtape).await()
        return docRef.id
    }

    override suspend fun agregarItemAMixtape(mixtapeId: String, item: ItemMixtapeDto) {
        val mixtapeRef = db.collection("mixtapes").document(mixtapeId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(mixtapeRef)
            val mixtape = snapshot.toObject(MixtapeDto::class.java)
            if (mixtape != null) {
                val currentItems = mixtape.items.toMutableList()
                if (currentItems.none { it.id == item.id }) {
                    currentItems.add(item)
                    transaction.update(mixtapeRef, "items", currentItems)
                }
            }
        }.await()
    }

    override suspend fun eliminarItemDeMixtape(mixtapeId: String, itemId: String) {
        val mixtapeRef = db.collection("mixtapes").document(mixtapeId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(mixtapeRef)
            val mixtape = snapshot.toObject(MixtapeDto::class.java)
            if (mixtape != null) {
                val currentItems = mixtape.items.filterNot { it.id == itemId }
                transaction.update(mixtapeRef, "items", currentItems)
            }
        }.await()
    }

    override suspend fun eliminarMixtape(mixtapeId: String) {
        db.collection("mixtapes").document(mixtapeId).delete().await()
    }

    override suspend fun toggleLikeMixtape(mixtapeId: String, usuarioId: String) {
        val mixtapeRef = db.collection("mixtapes").document(mixtapeId)
        val likeRef = mixtapeRef.collection("likes").document(usuarioId)

        db.runTransaction { transaction ->
            val likeDoc = transaction.get(likeRef)
            if (likeDoc.exists()) {
                transaction.delete(likeRef)
                transaction.update(mixtapeRef, "likesCount", FieldValue.increment(-1))
            } else {
                transaction.set(likeRef, mapOf("timestamp" to FieldValue.serverTimestamp()))
                transaction.update(mixtapeRef, "likesCount", FieldValue.increment(1))
            }
        }.await()
    }
}
