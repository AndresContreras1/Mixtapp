package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.NotificacionDto
import kotlinx.coroutines.flow.Flow

// Las notificaciones las crean las Cloud Functions; la app solo las lee y las marca leidas
interface NotificacionRemoteDataSource {
    // En vivo, la mas nueva primero: cada like, comentario o seguidor nuevo llega solo
    fun listenNotificaciones(usuarioId: String): Flow<List<NotificacionDto>>
    suspend fun marcarLeida(usuarioId: String, notificacionId: String): Unit
}
