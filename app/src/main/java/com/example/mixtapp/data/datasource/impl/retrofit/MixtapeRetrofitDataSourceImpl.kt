package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.dtos.CreateMixtapeDto
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// El backend no tiene mixtapes: solo funcionan con Firestore
class MixtapeRetrofitDataSourceImpl @Inject constructor() : MixtapeRemoteDataSource {

    // La lista llega vacia
    override fun listenMixtapesDeUsuario(usuarioId: String): Flow<List<MixtapeDto>> = flow {
        emit(emptyList<MixtapeDto>())
    }

    // Como una mixtape que no existe
    override fun listenMixtapeById(id: String): Flow<MixtapeDto?> = flow {
        emit(null)
    }

    // to-do: el backend no tiene la tabla de mixtapes
    override suspend fun crearMixtape(mixtape: CreateMixtapeDto): String = ""

    override suspend fun agregarItem(mixtapeId: String, item: ItemMixtapeDto) {
    }

    override suspend fun quitarItem(mixtapeId: String, itemId: String) {
    }

    override suspend fun eliminarMixtape(mixtapeId: String) {
    }
}
