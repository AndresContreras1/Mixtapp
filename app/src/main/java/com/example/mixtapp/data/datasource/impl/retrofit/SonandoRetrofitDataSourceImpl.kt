package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.SonandoRemoteDataSource
import com.example.mixtapp.data.dtos.CreateReaccionDto
import com.example.mixtapp.data.dtos.CreateSonandoDto
import com.example.mixtapp.data.dtos.SonandoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// El backend no tiene "sonando ahora": solo funciona con Firestore
class SonandoRetrofitDataSourceImpl @Inject constructor() : SonandoRemoteDataSource {

    // La lista llega vacia
    override fun listenSonando(currentUserId: String): Flow<List<SonandoDto>> = flow {
        emit(emptyList<SonandoDto>())
    }

    // to-do: el backend no tiene la tabla de sonando ni la de reacciones
    override suspend fun ponerSonando(usuarioId: String, sonando: CreateSonandoDto) {
    }

    override suspend fun quitarSonando(usuarioId: String) {
    }

    override suspend fun reaccionar(usuarioId: String, reaccion: CreateReaccionDto) {
    }
}
