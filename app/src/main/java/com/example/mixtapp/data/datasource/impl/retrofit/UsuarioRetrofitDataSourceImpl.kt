package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.datasource.services.UsuarioRetrofitService
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.UsuarioPerfilRetrofitDto
import javax.inject.Inject

const val USUARIO_DE_LA_APP_EN_EL_BACKEND = "1"

class UsuarioRetrofitDataSourceImpl @Inject constructor(
    private val service: UsuarioRetrofitService
) : UsuarioRemoteDataSource {

    override suspend fun getUsuarioById(id: String): UsuarioPerfilRetrofitDto {
        return service.getUsuarioById(idEnElBackend(id))
    }

    override suspend fun getUsuarios(): List<UsuarioPerfilRetrofitDto> {
        return emptyList()
    }

    override suspend fun getReviewsDeUsuario(id: String): List<ReviewConAlbumDto> {
        return service.getReviewsDeUsuario(idEnElBackend(id))
    }

    override suspend fun registrarUsuario(registrarUsuarioDto: RegistrarUsuarioDto, usuarioId: String) {
        // to-do: el backend no tiene una ruta para crear ni modificar usuarios
    }

    override fun idDelUsuarioEnLaBase(uid: String): String = idEnElBackend(uid)

    private fun idEnElBackend(id: String): String =
        if (id.toIntOrNull() != null) id else USUARIO_DE_LA_APP_EN_EL_BACKEND
}
