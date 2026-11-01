package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.MessagingRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.UsuarioRepository
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

// El repositorio de usuarios con mocks: se moquea la interfaz del data source, Authentication y el
// token de FCM, y se prueba el mapeo del perfil y los errores
class UsuarioRepositoryTest {

    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()
    private val messagingDataSource = mockk<MessagingRemoteDataSource>()

    private val repository = UsuarioRepository(usuarioDataSource, authDataSource, messagingDataSource)

    @Test
    fun getMiPerfil_bioYFotoNulas_textosVacios() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { usuarioDataSource.getUsuarioById("yo", any()) } returns UsuarioPerfilFirestoreDto().copy(
            id = "yo",
            nombre = "Ana",
            username = "ana",
            bio = null,
            fotoUrl = null,
            followersCount = 3,
        )
        //Act
        val result = repository.getMiPerfil()
        //Assert
        val perfil = result.getOrNull()
        Truth.assertThat(result.isSuccess).isTrue()
        Truth.assertThat(perfil?.nombre).isEqualTo("Ana")
        Truth.assertThat(perfil?.bio).isEmpty()
        Truth.assertThat(perfil?.fotoUrl).isEmpty()
        Truth.assertThat(perfil?.seguidores).isEqualTo(3)
        Truth.assertThat(perfil?.esMiPerfil).isTrue()
    }

    @Test
    fun getMiPerfil_sinDocumento_contenidoNoEncontrado() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { usuarioDataSource.getUsuarioById("yo", any()) } returns null
        //Act
        val result = repository.getMiPerfil()
        //Assert
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(ContenidoNoEncontradoException::class.java)
    }

    @Test
    fun buscarUsuarios_resultadoConMiCuenta_noMeIncluye() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { usuarioDataSource.searchUsuarios("An") } returns listOf(
            UsuarioPerfilFirestoreDto().copy(id = "yo", nombre = "Ana"),
            UsuarioPerfilFirestoreDto().copy(id = "otra", nombre = "Andrea"),
        )
        //Act
        val result = repository.buscarUsuarios("An")
        //Assert
        Truth.assertThat(result.getOrNull()?.map { it.id }).containsExactly("otra")
    }
}
