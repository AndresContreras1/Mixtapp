package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.ArtistaDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import kotlinx.coroutines.flow.Flow

// Contrato de los artistas: lo mismo que los albumes
interface ArtistaRemoteDataSource {
    suspend fun getArtistas(): List<ArtistaDto>
    // Si no existe retorna null: el repositorio decide el error
    suspend fun getArtistaById(id: String, currentUserId: String = ""): ArtistaDto?
    fun listenArtistas(): Flow<List<ArtistaDto>>
    fun listenArtistaById(id: String, currentUserId: String = ""): Flow<ArtistaDto?>
    suspend fun sendOrDeleteLike(artistaId: String, userId: String, copia: CopiaDeItemDto): Unit
}
