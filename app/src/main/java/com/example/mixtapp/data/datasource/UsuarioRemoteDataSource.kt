package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.ActualizarUsuarioDto
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioDtoGeneric
import kotlinx.coroutines.flow.Flow

// Contrato de los usuarios: cada implementacion retorna el DTO de su propia base.
// currentUserId es opcional para que Retrofit no tenga que usarlo
interface UsuarioRemoteDataSource {
    // Si no existe retorna null: el repositorio decide el error
    suspend fun getUsuarioById(id: String, currentUserId: String = ""): UsuarioDtoGeneric?
    suspend fun getReviewsDeUsuario(id: String, currentUserId: String = ""): List<ReviewConAlbumDto>
    // Crea los datos del usuario que tiene ese id
    suspend fun registrarUsuario(registrarUsuarioDto: RegistrarUsuarioDto, usuarioId: String): Unit
    suspend fun actualizarPerfil(usuarioId: String, actualizarUsuarioDto: ActualizarUsuarioDto): Unit
    suspend fun actualizarFotoDePerfil(usuarioId: String, fotoUrl: String): Unit
    // El token de FCM del celular con el que se inicio sesion
    suspend fun actualizarToken(usuarioId: String, token: String): Unit
    // Una sola funcion sigue o deja de seguir, segun si ya lo sigue
    suspend fun followOrUnfollow(currentUserId: String, targetUserId: String): Unit
    suspend fun getSeguidores(id: String): List<UsuarioDtoGeneric>
    suspend fun getSeguidos(id: String): List<UsuarioDtoGeneric>
    // Datos en vivo: cada vez que cambia el documento, sus resenas o sus listas, llega todo de
    // nuevo por el flujo
    fun listenUsuarioById(id: String, currentUserId: String = ""): Flow<UsuarioDtoGeneric>
    fun listenReviewsDeUsuario(id: String, currentUserId: String = ""): Flow<List<ReviewConAlbumDto>>
    fun listenSeguidores(id: String): Flow<List<UsuarioDtoGeneric>>
    fun listenSeguidos(id: String): Flow<List<UsuarioDtoGeneric>>
}
