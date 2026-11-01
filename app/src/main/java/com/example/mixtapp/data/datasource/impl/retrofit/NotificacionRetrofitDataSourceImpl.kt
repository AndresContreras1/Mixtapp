package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.NotificacionRemoteDataSource
import com.example.mixtapp.data.dtos.NotificacionDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// El backend no envia notificaciones: las crean las Cloud Functions en Firestore
class NotificacionRetrofitDataSourceImpl @Inject constructor() : NotificacionRemoteDataSource {

    // La lista llega vacia
    override fun listenNotificaciones(usuarioId: String): Flow<List<NotificacionDto>> = flow {
        emit(emptyList<NotificacionDto>())
    }

    // to-do: el backend no tiene notificaciones; como los likes, solo funcionan con Firestore
    override suspend fun marcarLeida(usuarioId: String, notificacionId: String) {
    }
}
