package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.CreateReaccionDto
import com.example.mixtapp.data.dtos.CreateSonandoDto
import com.example.mixtapp.data.dtos.SonandoDto
import kotlinx.coroutines.flow.Flow

// Lo que estan escuchando los usuarios: llega en vivo, igual que las reacciones 🔥
interface SonandoRemoteDataSource {
    // Lo que se puso a sonar desde esa fecha (texto ISO), lo mas reciente primero. Con mi uid se
    // marca a cuales ya les reaccione
    fun listenSonando(desde: String, currentUserId: String): Flow<List<SonandoDto>>

    // Reemplaza lo que estaba sonando y vuelve a empezar las reacciones en 0
    suspend fun ponerSonando(usuarioId: String, sonando: CreateSonandoDto): Unit

    suspend fun quitarSonando(usuarioId: String): Unit

    // Pone mi reaccion o la quita si ya estaba, y cambia el contador en la misma transaccion
    suspend fun reaccionar(usuarioId: String, reaccion: CreateReaccionDto): Unit
}
