package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.CancionDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import kotlinx.coroutines.flow.Flow

// Contrato de las canciones: lo mismo que los albumes
interface CancionRemoteDataSource {
    suspend fun getCanciones(): List<CancionDto>
    // Si no existe retorna null: el repositorio decide el error
    suspend fun getCancionById(id: String, currentUserId: String = ""): CancionDto?
    fun listenCanciones(): Flow<List<CancionDto>>
    fun listenCancionById(id: String, currentUserId: String = ""): Flow<CancionDto?>
    suspend fun sendOrDeleteLike(cancionId: String, userId: String, copia: CopiaDeItemDto): Unit
}
