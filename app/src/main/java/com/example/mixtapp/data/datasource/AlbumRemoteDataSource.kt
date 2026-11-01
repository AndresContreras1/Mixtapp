package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import kotlinx.coroutines.flow.Flow

// Contrato de los albumes. currentUserId es opcional para que Retrofit no tenga que usarlo
interface AlbumRemoteDataSource {
    suspend fun getAlbumes(): List<AlbumDto>
    // Si no existe retorna null: el repositorio decide el error. Con mi uid se sabe si le di like
    suspend fun getAlbumById(id: String, currentUserId: String = ""): AlbumDto?
    // Datos en vivo: un like en otro celular cambia likesCount y la lista llega de nuevo
    fun listenAlbumes(): Flow<List<AlbumDto>>
    // El album en vivo, con mi like. Si no existe llega null
    fun listenAlbumById(id: String, currentUserId: String = ""): Flow<AlbumDto?>
    // Una sola funcion da el like o lo quita, segun si ya existe. La copia queda en mis likes
    suspend fun sendOrDeleteLike(albumId: String, userId: String, copia: CopiaDeItemDto): Unit
}
