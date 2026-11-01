package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.ActualizarUsuarioDto
import com.example.mixtapp.data.dtos.CopiaDeItemDto
import com.example.mixtapp.data.dtos.ItemDeUsuarioDto
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewDto
import com.example.mixtapp.data.dtos.UsuarioDtoGeneric
import kotlinx.coroutines.flow.Flow

// Contrato de los usuarios: cada implementacion retorna el DTO de su propia base.
// currentUserId es opcional para que Retrofit no tenga que usarlo
interface UsuarioRemoteDataSource {
    // Si no existe retorna null: el repositorio decide el error
    suspend fun getUsuarioById(id: String, currentUserId: String = ""): UsuarioDtoGeneric?
    suspend fun getReviewsDeUsuario(id: String, currentUserId: String = ""): List<ReviewDto>
    // Los usuarios cuyo nombre empieza por el texto, hasta 10
    suspend fun searchUsuarios(texto: String): List<UsuarioDtoGeneric>
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
    // Una sola funcion guarda el item o lo quita de mis guardados, segun si ya esta
    suspend fun sendOrDeleteGuardado(usuarioId: String, itemId: String, copia: CopiaDeItemDto): Unit
    // Datos en vivo: cada vez que cambia el documento, sus resenas o sus listas, llega todo de
    // nuevo por el flujo
    // Si el usuario no tiene documento llega null: el repositorio decide el error
    fun listenUsuarioById(id: String, currentUserId: String = ""): Flow<UsuarioDtoGeneric?>
    fun listenReviewsDeUsuario(id: String, currentUserId: String = ""): Flow<List<ReviewDto>>
    fun listenSeguidores(id: String): Flow<List<UsuarioDtoGeneric>>
    fun listenSeguidos(id: String): Flow<List<UsuarioDtoGeneric>>
    // Los items a los que le dio like y los que guardo, el mas nuevo primero
    fun listenLikes(id: String): Flow<List<ItemDeUsuarioDto>>
    fun listenGuardados(id: String): Flow<List<ItemDeUsuarioDto>>
    // Si ese item esta en mis guardados, en vivo: el boton de guardar cambia al tocarlo
    fun listenGuardado(usuarioId: String, itemId: String): Flow<Boolean>
}
