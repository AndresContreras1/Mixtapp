package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.DueloRemoteDataSource
import com.example.mixtapp.data.dtos.CreateDueloDto
import com.example.mixtapp.data.dtos.CreateVotoDto
import com.example.mixtapp.data.dtos.DueloDto
import com.example.mixtapp.data.dtos.VotoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// El backend no tiene duelos: solo funcionan con Firestore
class DueloRetrofitDataSourceImpl @Inject constructor() : DueloRemoteDataSource {

    // Como un duelo en el que nadie ha votado
    override fun listenDuelo(dueloId: String): Flow<DueloDto?> = flow {
        emit(null)
    }

    // La lista llega vacia
    override fun listenVotos(dueloId: String): Flow<List<VotoDto>> = flow {
        emit(emptyList<VotoDto>())
    }

    // to-do: el backend no tiene la tabla de duelos ni la de votos
    override suspend fun votar(dueloId: String, duelo: CreateDueloDto, usuarioId: String, voto: CreateVotoDto) {
    }
}
