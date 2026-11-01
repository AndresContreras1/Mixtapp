package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.HistoriaRemoteDataSource
import com.example.mixtapp.data.dtos.HistoriaDto
import javax.inject.Inject

// El backend no tiene la tabla de historias: los datos curiosos por secciones estan en Firestore
class HistoriaRetrofitDataSourceImpl @Inject constructor() : HistoriaRemoteDataSource {

    // to-do: el backend no tiene historias; como las notificaciones, solo funcionan con Firestore.
    // Retorna null y la pantalla muestra que el item no tiene datos curiosos
    override suspend fun getHistoria(itemId: String): HistoriaDto? = null
}
