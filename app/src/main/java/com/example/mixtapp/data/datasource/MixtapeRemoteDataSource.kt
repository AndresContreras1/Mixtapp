package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import kotlinx.coroutines.flow.Flow

interface MixtapeRemoteDataSource {
    fun escucharMixtapesDelUsuario(usuarioId: String, currentUserId: String? = null): Flow<List<MixtapeDto>>
    fun escucharMixtapePorId(id: String, currentUserId: String? = null): Flow<MixtapeDto?>
    suspend fun crearMixtape(
        nombre: String,
        descripcion: String?,
        usuarioId: String,
        usuarioNombre: String,
        usuarioFotoUrl: String?,
        primerItem: ItemMixtapeDto? = null
    ): String
    suspend fun agregarItemAMixtape(mixtapeId: String, item: ItemMixtapeDto)
    suspend fun eliminarItemDeMixtape(mixtapeId: String, itemId: String)
    suspend fun eliminarMixtape(mixtapeId: String)
    suspend fun toggleLikeMixtape(mixtapeId: String, usuarioId: String)
}
