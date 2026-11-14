package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.HistoriaRemoteDataSource
import com.example.mixtapp.data.dtos.HistoriaDto
import javax.inject.Inject

// El backend guarda los datos curiosos como una lista de parrafos en canciones y artistas, sin las
// secciones de la historia: esas estan solo en Firestore
class HistoriaRetrofitDataSourceImpl @Inject constructor() : HistoriaRemoteDataSource {

    // to-do: el backend no tiene historias por secciones; como las notificaciones, solo funcionan con
    // Firestore. Retorna null y la pantalla muestra que el item no tiene datos curiosos
    override suspend fun getHistoria(itemId: String): HistoriaDto? = null
}
