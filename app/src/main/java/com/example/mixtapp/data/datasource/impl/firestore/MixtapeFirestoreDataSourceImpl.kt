package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.dtos.CreateItemMixtapeDto
import com.example.mixtapp.data.dtos.CreateMixtapeDto
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
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

// Cuantas portadas guarda la mixtape para el mosaico de su tarjeta
private const val MAX_PORTADAS = 4

class MixtapeFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : MixtapeRemoteDataSource {

    // El listener escucha las mixtapes de un usuario con whereEqualTo. Si llega el id de un item, en
    // cuales ya esta se busca con exists() en su subcoleccion cada vez que llega la lista
    override fun listenMixtapesDeUsuario(usuarioId: String, itemId: String): Flow<List<MixtapeDto>> = callbackFlow {
        val listenerRegistration = db.collection("mixtapes")
            .whereEqualTo("usuarioId", usuarioId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val mixtapes = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(MixtapeDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(mixtapes).isSuccess // envia la lista al flujo
                }
            }

        // Cuando se sale de la pantalla nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }.map { mixtapes -> conElItem(mixtapes = mixtapes, itemId = itemId) }

    // Si la borran mientras se ve, toObject da null y se envia null: el repositorio decide el error
    override fun listenMixtapeById(id: String): Flow<MixtapeDto?> = callbackFlow {
        val listenerRegistration = db.collection("mixtapes").document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val mixtape = snapshot.toObject(MixtapeDto::class.java)?.copy(id = snapshot.id)
                    trySend(mixtape).isSuccess // envia la mixtape (o null) al flujo
                }
            }

        // Cuando se sale del detalle nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }

    // Los items de la subcoleccion: el id de cada documento es el id del item
    override fun listenItemsDeMixtape(mixtapeId: String): Flow<List<ItemMixtapeDto>> = callbackFlow {
        val listenerRegistration = db.collection("mixtapes").document(mixtapeId).collection("items")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error) // cierra el flujo con error
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ItemMixtapeDto::class.java)?.copy(id = doc.id)
                    }
                    trySend(items).isSuccess // envia la lista al flujo
                }
            }

        // Cuando se sale del detalle nadie escucha el flujo y se quita el listener
        awaitClose { listenerRegistration.remove() }
    }

    // add crea el documento con un id aleatorio, como un comentario
    override suspend fun crearMixtape(mixtape: CreateMixtapeDto): String {
        val documento = db.collection("mixtapes").add(mixtape).await()

        return documento.id
    }

    // El item y el contador van en una transaccion, como un like: o se hacen los dos o ninguno.
    // Mientras haya menos de cuatro portadas, la del item nuevo entra al mosaico
    override suspend fun agregarItem(mixtapeId: String, itemId: String, item: CreateItemMixtapeDto): Boolean {
        val mixtapeRef = db.collection("mixtapes").document(mixtapeId)
        val itemRef = mixtapeRef.collection("items").document(itemId)

        return db.runTransaction { transaction ->
            val mixtape = transaction.get(mixtapeRef).toObject(MixtapeDto::class.java)
                ?: return@runTransaction false
            val itemDoc = transaction.get(itemRef)

            // Si ya esta no se escribe nada
            if (!itemDoc.exists()) {
                transaction.set(itemRef, item)
                transaction.update(mixtapeRef, "itemsCount", FieldValue.increment(1))
                if (mixtape.portadas.size < MAX_PORTADAS) {
                    transaction.update(mixtapeRef, "portadas", mixtape.portadas + item.imagenUrl)
                }
            }
            true
        }.await()
    }

    // Igual que agregar: el item, el contador y la portada cambian juntos
    override suspend fun quitarItem(mixtapeId: String, itemId: String): Boolean {
        val mixtapeRef = db.collection("mixtapes").document(mixtapeId)
        val itemRef = mixtapeRef.collection("items").document(itemId)

        return db.runTransaction { transaction ->
            val mixtape = transaction.get(mixtapeRef).toObject(MixtapeDto::class.java)
                ?: return@runTransaction false
            val item = transaction.get(itemRef).toObject(ItemMixtapeDto::class.java)

            if (item != null) {
                transaction.delete(itemRef)
                transaction.update(mixtapeRef, "itemsCount", FieldValue.increment(-1))
                transaction.update(mixtapeRef, "portadas", mixtape.portadas - item.imagenUrl)
            }
            true
        }.await()
    }

    // Borrar el documento no borra su subcoleccion: los items se borran en el mismo batch
    override suspend fun eliminarMixtape(mixtapeId: String) {
        val mixtapeRef = db.collection("mixtapes").document(mixtapeId)
        val items = mixtapeRef.collection("items").get().await()
        val batch = db.batch()

        for (item in items) batch.delete(item.reference)
        batch.delete(mixtapeRef)

        batch.commit().await()
    }

    // En cuales mixtapes ya esta el item: se pregunta a todas a la vez y se espera la mas lenta,
    // como los likes. Sin item no se consulta nada
    private suspend fun conElItem(mixtapes: List<MixtapeDto>, itemId: String): List<MixtapeDto> =
        coroutineScope {
            if (itemId.isEmpty()) return@coroutineScope mixtapes

            val pendientes = mixtapes.map { dto ->
                async {
                    dto.tieneElItem = db.collection("mixtapes").document(dto.id)
                        .collection("items").document(itemId)
                        .get().await().exists()
                    dto
                }
            }

            pendientes.map { it.await() }
        }
}
