package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.ReproductorDataSource
import com.example.mixtapp.data.datasource.VistaPreviaRemoteDataSource
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.repository.ReproductorRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.SinVistaPreviaException
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.io.IOException

// El fragmento de 30 segundos con mocks: Deezer y el reproductor se reemplazan y se revisa que el
// repositorio decida el error
class ReproductorRepositoryTest {

    private val vistaPreviaDataSource = mockk<VistaPreviaRemoteDataSource>()
    private val reproductorDataSource = mockk<ReproductorDataSource>()

    private val repository = ReproductorRepository(vistaPreviaDataSource, reproductorDataSource)

    private val cancion = LocalPreviewProvider.emptinessMachine

    @Test
    fun getVistaPrevia_deezerLaTiene_entregaElEnlace() = runTest {
        //Arrange
        coEvery { vistaPreviaDataSource.getVistaPrevia("${cancion.artista} ${cancion.titulo}") } returns "https://preview.mp3"
        //Act
        val result = repository.getVistaPrevia(cancion)
        //Assert
        assertThat(result.getOrNull()).isEqualTo("https://preview.mp3")
    }

    @Test
    fun getVistaPrevia_deezerNoLaTiene_sinVistaPrevia() = runTest {
        //Arrange
        coEvery { vistaPreviaDataSource.getVistaPrevia(any()) } returns null
        //Act
        val result = repository.getVistaPrevia(cancion)
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(SinVistaPreviaException::class.java)
    }

    @Test
    fun getVistaPrevia_sinInternet_sinConexion() = runTest {
        //Arrange
        coEvery { vistaPreviaDataSource.getVistaPrevia(any()) } throws IOException("sin red")
        //Act
        val result = repository.getVistaPrevia(cancion)
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(SinConexionException::class.java)
    }

    @Test
    fun escuchar_suena_entregaLosEstados() = runTest {
        //Arrange
        every { reproductorDataSource.reproducir("https://preview.mp3") } returns
            flowOf(EstadoDeReproduccion.Cargando, EstadoDeReproduccion.Sonando)
        //Act
        val estados = repository.escuchar("https://preview.mp3").toList()
        //Assert
        assertThat(estados).containsExactly(EstadoDeReproduccion.Cargando, EstadoDeReproduccion.Sonando).inOrder()
    }

    @Test
    fun escuchar_elAudioNoAbre_sinConexion() = runTest {
        //Arrange
        every { reproductorDataSource.reproducir(any()) } returns flow { throw IOException("MediaPlayer") }
        //Act
        val error = runCatching { repository.escuchar("https://preview.mp3").toList() }.exceptionOrNull()
        //Assert
        assertThat(error).isInstanceOf(SinConexionException::class.java)
    }
}
