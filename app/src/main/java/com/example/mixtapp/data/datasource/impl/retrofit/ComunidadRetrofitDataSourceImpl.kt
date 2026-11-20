package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.ComunidadRemoteDataSource
import com.example.mixtapp.data.dtos.ChatDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import com.example.mixtapp.data.dtos.CreateMensajeDto
import com.example.mixtapp.data.dtos.ForoDto
import com.example.mixtapp.data.dtos.ItemDeUsuarioDto
import com.example.mixtapp.data.dtos.MensajeDto
import com.example.mixtapp.data.dtos.UsuarioDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

// El backend no tiene la comunidad: solo funciona con Firestore. Las listas llegan vacias
class ComunidadRetrofitDataSourceImpl @Inject constructor() : ComunidadRemoteDataSource {

    override suspend fun getUsuarios(limite: Int): List<UsuarioPerfilFirestoreDto> = emptyList()

    override suspend fun getLikes(usuarioId: String): List<ItemDeUsuarioDto> = emptyList()

    override fun listenForos(limite: Int): Flow<List<ForoDto>> = flow {
        emit(emptyList<ForoDto>())
    }

    override fun listenMensajesDelForo(itemId: String): Flow<List<MensajeDto>> = flow {
        emit(emptyList<MensajeDto>())
    }

    override suspend fun publicarEnForo(itemId: String, item: CopiaDeItemDto, mensaje: CreateMensajeDto) {
        // to-do: el backend no tiene una ruta para los foros
    }

    override fun listenChats(usuarioId: String): Flow<List<ChatDto>> = flow {
        emit(emptyList<ChatDto>())
    }

    override fun listenMensajesDelChat(chatId: String): Flow<List<MensajeDto>> = flow {
        emit(emptyList<MensajeDto>())
    }

    override suspend fun enviarMensaje(chatId: String, participantes: List<UsuarioDto>, mensaje: CreateMensajeDto) {
        // to-do: el backend no tiene una ruta para los mensajes
    }
}
