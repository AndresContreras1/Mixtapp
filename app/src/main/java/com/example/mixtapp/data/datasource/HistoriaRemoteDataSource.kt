package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.HistoriaDto

// Contrato de los datos curiosos de cada item. No cambian mientras se usa la app: se leen una vez,
// sin listener
interface HistoriaRemoteDataSource {
    // Si el item no tiene historia retorna null: el repositorio decide el error
    suspend fun getHistoria(itemId: String): HistoriaDto?
}
