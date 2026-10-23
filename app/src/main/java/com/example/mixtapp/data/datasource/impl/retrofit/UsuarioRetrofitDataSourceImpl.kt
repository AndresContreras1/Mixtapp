package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.datasource.services.UsuarioRetrofitService
import com.example.mixtapp.data.dtos.ActualizarUsuarioDto
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilRetrofitDto
import javax.inject.Inject

// Los usuarios de Firebase no existen en el backend: el usuario de la app se quema como el 1
const val USUARIO_DE_LA_APP_EN_EL_BACKEND = "1"

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

    // Los ids del backend son numeros; el uid de Firebase no, asi que ese se cambia por el 1
    private fun idEnElBackend(id: String): String =
        if (id.toIntOrNull() != null) id else USUARIO_DE_LA_APP_EN_EL_BACKEND
}
