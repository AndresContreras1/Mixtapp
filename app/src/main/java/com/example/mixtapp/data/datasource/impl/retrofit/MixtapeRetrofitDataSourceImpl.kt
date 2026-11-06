package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class MixtapeRetrofitDataSourceImpl @Inject constructor() : MixtapeRemoteDataSource {
    override fun escucharMixtapesDelUsuario(
        usuarioId: String,
        currentUserId: String?
    ): Flow<List<MixtapeDto>> = flowOf(emptyList())

    override fun escucharMixtapePorId(
        id: String,
        currentUserId: String?
    ): Flow<MixtapeDto?> = flowOf(null)

    override suspend fun crearMixtape(
        nombre: String,
        descripcion: String?,
        usuarioId: String,
        usuarioNombre: String,
        usuarioFotoUrl: String?,
        primerItem: ItemMixtapeDto?
    ): String = ""

    override suspend fun agregarItemAMixtape(mixtapeId: String, item: ItemMixtapeDto) {}

    override suspend fun eliminarItemDeMixtape(mixtapeId: String, itemId: String) {}

    override suspend fun eliminarMixtape(mixtapeId: String) {}

    override suspend fun toggleLikeMixtape(mixtapeId: String, usuarioId: String) {}
}
