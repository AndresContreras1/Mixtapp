package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.dtos.CreateItemMixtapeDto
import com.example.mixtapp.data.dtos.CreateMixtapeDto
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// El backend no tiene mixtapes: solo funcionan con Firestore
class MixtapeRetrofitDataSourceImpl @Inject constructor() : MixtapeRemoteDataSource {

    // La lista llega vacia
    override fun listenMixtapesDeUsuario(usuarioId: String, itemId: String): Flow<List<MixtapeDto>> = flow {
        emit(emptyList<MixtapeDto>())
    }

    // Como una mixtape que no existe
    override fun listenMixtapeById(id: String): Flow<MixtapeDto?> = flow {
        emit(null)
    }

    override fun listenItemsDeMixtape(mixtapeId: String): Flow<List<ItemMixtapeDto>> = flow {
        emit(emptyList<ItemMixtapeDto>())
    }

    // to-do: el backend no tiene la tabla de mixtapes
    override suspend fun crearMixtape(mixtape: CreateMixtapeDto): String = ""

    // Como si la mixtape no existiera: el repositorio responde que no se encontro
    override suspend fun agregarItem(mixtapeId: String, itemId: String, item: CreateItemMixtapeDto): Boolean = false

    override suspend fun quitarItem(mixtapeId: String, itemId: String): Boolean = false

    override suspend fun eliminarMixtape(mixtapeId: String) {
    }
}
