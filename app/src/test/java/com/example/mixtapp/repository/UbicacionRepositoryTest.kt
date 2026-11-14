package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.UbicacionDataSource
import com.example.mixtapp.data.dtos.UbicacionDto
import com.example.mixtapp.data.model.Coordenadas
import com.example.mixtapp.data.repository.SinPermisoDeUbicacionException
import com.example.mixtapp.data.repository.UbicacionNoDisponibleException
import com.example.mixtapp.data.repository.UbicacionRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

// La ubicacion con el data source moqueado: el repositorio mapea el DTO y decide el error
class UbicacionRepositoryTest {

    private val ubicacionDataSource = mockk<UbicacionDataSource>()
    private val repository = UbicacionRepository(ubicacionDataSource)

    @Test
    fun getUbicacionActual_conUbicacion_coordenadas() = runTest {
        //Arrange
        coEvery { ubicacionDataSource.getUbicacionActual() } returns UbicacionDto(latitud = 6.24, longitud = -75.58)
        //Act
        val result = repository.getUbicacionActual()
        //Assert
        assertThat(result.getOrNull()).isEqualTo(Coordenadas(latitud = 6.24, longitud = -75.58))
    }

    @Test
    fun getUbicacionActual_sinDato_ubicacionNoDisponible() = runTest {
        //Arrange: sin permiso o sin GPS el data source retorna null
        coEvery { ubicacionDataSource.getUbicacionActual() } returns null
        //Act
        val result = repository.getUbicacionActual()
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(UbicacionNoDisponibleException::class.java)
    }

    @Test
    fun getUbicacionActual_quitaronElPermiso_sinPermisoDeUbicacion() = runTest {
        //Arrange: Android lanza SecurityException si el permiso se quito mientras se pedia
        coEvery { ubicacionDataSource.getUbicacionActual() } throws SecurityException("sin permiso")
        //Act
        val result = repository.getUbicacionActual()
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(SinPermisoDeUbicacionException::class.java)
    }
}
