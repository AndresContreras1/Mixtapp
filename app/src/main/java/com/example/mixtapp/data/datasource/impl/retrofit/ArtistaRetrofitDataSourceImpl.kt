package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.ArtistaRemoteDataSource
import com.example.mixtapp.data.datasource.services.ArtistaRetrofitService
import com.example.mixtapp.data.dtos.ArtistaDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ArtistaRetrofitDataSourceImpl @Inject constructor(
    private val service: ArtistaRetrofitService
) : ArtistaRemoteDataSource {

    override suspend fun getArtistas(): List<ArtistaDto> {
        return service.getArtistas()
    }

    // El backend no tiene likes: el corazon queda vacio
    override suspend fun getArtistaById(id: String, currentUserId: String): ArtistaDto {
        return service.getArtistaById(id)
    }

    // Con HTTP no hay una conexion continua: flow { emit() } manda una sola vez lo que responde el backend
    override fun listenArtistas(): Flow<List<ArtistaDto>> = flow {
        emit(getArtistas())
    }

    override fun listenArtistaById(id: String, currentUserId: String): Flow<ArtistaDto> = flow {
        emit(getArtistaById(id = id, currentUserId = currentUserId))
    }

    // to-do: el backend no tiene likes; como en clase, solo funcionan con Firestore
    override suspend fun sendOrDeleteLike(artistaId: String, userId: String, copia: CopiaDeItemDto) {
    }
}
