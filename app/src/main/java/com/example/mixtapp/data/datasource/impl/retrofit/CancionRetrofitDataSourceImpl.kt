package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.CancionRemoteDataSource
import com.example.mixtapp.data.datasource.services.CancionRetrofitService
import com.example.mixtapp.data.dtos.CancionDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CancionRetrofitDataSourceImpl @Inject constructor(
    private val service: CancionRetrofitService
) : CancionRemoteDataSource {

    override suspend fun getCanciones(): List<CancionDto> {
        return service.getCanciones()
    }

    // El backend no tiene likes: el corazon queda vacio
    override suspend fun getCancionById(id: String, currentUserId: String): CancionDto {
        return service.getCancionById(id)
    }

    // Con HTTP no hay una conexion continua: flow { emit() } manda una sola vez lo que responde el backend
    override fun listenCanciones(): Flow<List<CancionDto>> = flow {
        emit(getCanciones())
    }

    override fun listenCancionById(id: String, currentUserId: String): Flow<CancionDto> = flow {
        emit(getCancionById(id = id, currentUserId = currentUserId))
    }

    // to-do: el backend no tiene likes; como en clase, solo funcionan con Firestore
    override suspend fun sendOrDeleteLike(cancionId: String, userId: String, copia: CopiaDeItemDto) {
    }
}
