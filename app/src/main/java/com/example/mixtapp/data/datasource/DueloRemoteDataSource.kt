package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.CreateDueloDto
import com.example.mixtapp.data.dtos.CreateVotoDto
import com.example.mixtapp.data.dtos.DueloDto
import com.example.mixtapp.data.dtos.VotoDto
import kotlinx.coroutines.flow.Flow

// Los duelos del dia: cada voto llega en vivo a todos los que tienen la pantalla abierta
interface DueloRemoteDataSource {
    // null mientras nadie haya votado en ese duelo (todavia no existe el documento)
    fun listenDuelo(dueloId: String): Flow<DueloDto?>

    // Los votos con su opinion, el mas nuevo primero
    fun listenVotos(dueloId: String): Flow<List<VotoDto>>

    // Crea el duelo si es el primer voto, suma el voto y, si ya habia votado por el otro, se lo resta
    suspend fun votar(dueloId: String, duelo: CreateDueloDto, usuarioId: String, voto: CreateVotoDto): Unit
}
