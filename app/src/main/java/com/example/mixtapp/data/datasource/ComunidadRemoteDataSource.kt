package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.ChatDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import com.example.mixtapp.data.dtos.CreateMensajeDto
import com.example.mixtapp.data.dtos.ForoDto
import com.example.mixtapp.data.dtos.ItemDeUsuarioDto
import com.example.mixtapp.data.dtos.MensajeDto
import com.example.mixtapp.data.dtos.UsuarioDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import kotlinx.coroutines.flow.Flow

// La comunidad: las personas con gustos parecidos, los foros de cada item y los mensajes directos
interface ComunidadRemoteDataSource {
    // Los usuarios de la app, hasta el limite, para calcular con quien hay afinidad
    suspend fun getUsuarios(limite: Int): List<UsuarioPerfilFirestoreDto>
    // Los items a los que le dio like, una vez
    suspend fun getLikes(usuarioId: String): List<ItemDeUsuarioDto>

    // Los foros con actividad, en vivo
    fun listenForos(limite: Int): Flow<List<ForoDto>>
    // Los mensajes de un foro, en vivo, el mas viejo primero
    fun listenMensajesDelForo(itemId: String): Flow<List<MensajeDto>>
    // Publica el mensaje y deja al dia el documento del foro (lo crea si es el primero)
    suspend fun publicarEnForo(itemId: String, item: CopiaDeItemDto, mensaje: CreateMensajeDto)

    // Mis chats, en vivo
    fun listenChats(usuarioId: String): Flow<List<ChatDto>>
    // Los mensajes de un chat, en vivo, el mas viejo primero
    fun listenMensajesDelChat(chatId: String): Flow<List<MensajeDto>>
    // Envia el mensaje y deja al dia el documento del chat (lo crea si es el primero)
    suspend fun enviarMensaje(chatId: String, participantes: List<UsuarioDto>, mensaje: CreateMensajeDto)
}
