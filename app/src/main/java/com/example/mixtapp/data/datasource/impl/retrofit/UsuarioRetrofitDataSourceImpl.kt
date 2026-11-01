package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.datasource.services.UsuarioRetrofitService
import com.example.mixtapp.data.dtos.ActualizarUsuarioDto
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilRetrofitDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UsuarioRetrofitDataSourceImpl @Inject constructor(
    private val service: UsuarioRetrofitService
) : UsuarioRemoteDataSource {

    override suspend fun getUsuarioById(id: String, currentUserId: String): UsuarioPerfilRetrofitDto {
        return service.getUsuarioById(idEnElBackend(id))
    }

    override suspend fun getReviewsDeUsuario(id: String, currentUserId: String): List<ReviewConAlbumDto> {
        return service.getReviewsDeUsuario(idEnElBackend(id))
    }

    override suspend fun registrarUsuario(registrarUsuarioDto: RegistrarUsuarioDto, usuarioId: String) {
        // to-do: el backend no tiene una ruta para crear ni modificar usuarios
    }

    override suspend fun actualizarPerfil(usuarioId: String, actualizarUsuarioDto: ActualizarUsuarioDto) {
        // to-do: el backend no tiene una ruta para modificar usuarios
    }

    override suspend fun actualizarFotoDePerfil(usuarioId: String, fotoUrl: String) {
        // to-do: el backend no tiene una ruta para modificar usuarios
    }

    override suspend fun actualizarToken(usuarioId: String, token: String) {
        // to-do: el backend no tiene una ruta para modificar usuarios ni envia notificaciones
    }

    override suspend fun followOrUnfollow(currentUserId: String, targetUserId: String) {
        // to-do: el backend no tiene seguidores; como en clase, solo funcionan con Firestore
    }

    // El backend no tiene seguidores: las listas quedan vacias
    override suspend fun getSeguidores(id: String): List<UsuarioPerfilRetrofitDto> {
        return emptyList()
    }

    override suspend fun getSeguidos(id: String): List<UsuarioPerfilRetrofitDto> {
        return emptyList()
    }

    // Con HTTP no hay una conexion continua: cada flow { emit() } manda una sola vez lo que responde el backend
    override fun listenUsuarioById(id: String, currentUserId: String): Flow<UsuarioPerfilRetrofitDto> = flow {
        emit(getUsuarioById(id = id, currentUserId = currentUserId))
    }

    override fun listenReviewsDeUsuario(id: String, currentUserId: String): Flow<List<ReviewConAlbumDto>> = flow {
        emit(getReviewsDeUsuario(id = id, currentUserId = currentUserId))
    }

    // El backend no tiene seguidores: las listas llegan vacias
    override fun listenSeguidores(id: String): Flow<List<UsuarioPerfilRetrofitDto>> = flow {
        emit(getSeguidores(id = id))
    }

    override fun listenSeguidos(id: String): Flow<List<UsuarioPerfilRetrofitDto>> = flow {
        emit(getSeguidos(id = id))
    }

    // Los ids del backend son numeros; el uid de Firebase no, asi que ese se cambia por el 1
    private fun idEnElBackend(id: String): String =
        if (id.toIntOrNull() != null) id else USUARIO_DE_LA_APP_EN_EL_BACKEND
}
