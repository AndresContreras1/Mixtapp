package com.example.mixtapp.repository

import android.net.Uri
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.StorageRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.repository.SinSesionException
import com.example.mixtapp.data.repository.StorageRepository
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

// La foto de perfil con mocks: se sube a Storage, queda en Authentication y se copia en mi documento
class StorageRepositoryTest {

    private val storageDataSource = mockk<StorageRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()
    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val uri = mockk<Uri>()

    private val repository = StorageRepository(storageDataSource, authDataSource, usuarioDataSource)

    // La foto sube bien a Storage y a Authentication en todas las pruebas que tienen sesion
    private fun subeLaFoto() {
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { storageDataSource.uploadImage(path = "profileImages/yo.jpg", uri = uri) } returns "https://foto"
        coEvery { authDataSource.updateProfileImage(photoUrl = "https://foto") } returns Unit
    }

    @Test
    fun uploadProfileImage_conDocumento_retornaLaUrl() = runTest {
        //Arrange
        subeLaFoto()
        coEvery { usuarioDataSource.actualizarFotoDePerfil("yo", "https://foto") } returns Unit
        //Act
        val result = repository.uploadProfileImage(uri = uri)
        //Assert
        Truth.assertThat(result.getOrNull()).isEqualTo("https://foto")
    }

    @Test
    fun uploadProfileImage_sinSesion_sinSesionException() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser } returns null
        //Act
        val result = repository.uploadProfileImage(uri = uri)
        //Assert
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }
}
