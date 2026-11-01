package com.example.mixtapp.data.datasource.impl.firestore

import com.example.mixtapp.data.datasource.HistoriaRemoteDataSource
import com.example.mixtapp.data.dtos.HistoriaDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class HistoriaFirestoreDataSourceImpl @Inject constructor(
    private val db: FirebaseFirestore
) : HistoriaRemoteDataSource {

    // historias/{itemId}: el documento tiene el mismo id del album, la cancion o el artista
    override suspend fun getHistoria(itemId: String): HistoriaDto? {
        val respuesta = db.collection("historias").document(itemId).get().await()
        // Si el documento no existe, toObject da null y se retorna null: el repositorio decide el error
        return respuesta.toObject(HistoriaDto::class.java)
    }
}
