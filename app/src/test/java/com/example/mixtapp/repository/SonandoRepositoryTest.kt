package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.SonandoRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.SonandoDto
import com.example.mixtapp.data.dtos.UsuarioDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.dtos.toSonandoUi
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.repository.ErrorInesperadoException
import com.example.mixtapp.data.repository.SeguirseASiMismoException
import com.example.mixtapp.data.repository.SinSesionException
import com.example.mixtapp.data.repository.SonandoRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

// "Sonando ahora" con mocks: los flujos del data source se reemplazan por flowOf y se revisa que el
// repositorio deje solo lo mio y lo de quienes sigo, de las ultimas horas, y lo mapee
class SonandoRepositoryTest {

    private val sonandoDataSource = mockk<SonandoRemoteDataSource>()
    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()

    private val repository = SonandoRepository(sonandoDataSource, usuarioDataSource, authDataSource)

    // Una hora fija: asi la prueba da lo mismo cualquier dia que se corra
    private val ahora = Instant.parse("2026-11-13T12:00:00Z")

    private fun sonando(usuarioId: String, actualizadoEn: String, reacciones: Int = 0) = SonandoDto().copy(
        id = usuarioId,
        usuario = UsuarioDto(id = usuarioId, nombre = "Nombre $usuarioId", fotoUrl = null),
        itemId = "album_1",
        tipo = "album",
        titulo = "Album 1",
        subtitulo = "Artista 1",
        reaccionesCount = reacciones,
        actualizadoEn = actualizadoEn,
    )

    // Casi todas las pruebas son con la sesion iniciada: la que no, cambia el mock
    @Before
    fun setUp() {
        coEvery { authDataSource.currentUser?.uid } returns "yo"
    }

    @Test
    fun getSonandoLive_variosUsuarios_soloLoMioYLoDeQuienesSigo() = runTest {
        //Arrange: sigo a laura; luis no lo sigo
        coEvery { sonandoDataSource.listenSonando("yo") } returns flowOf(
            listOf(
                sonando("laura", "2026-11-13T11:00:00Z"),
                sonando("luis", "2026-11-13T11:30:00Z"),
                sonando("yo", "2026-11-13T10:00:00Z"),
            )
        )
        coEvery { usuarioDataSource.listenSeguidos("yo") } returns flowOf(
            listOf(UsuarioPerfilFirestoreDto().copy(id = "laura"))
        )
        //Act
        val fila = repository.getSonandoLive(ahora = ahora).first()
        //Assert: lo mio primero
        assertThat(fila.map { it.usuarioId }).containsExactly("yo", "laura").inOrder()
        assertThat(fila[0].esMio).isTrue()
    }

    @Test
    fun getSonandoLive_hace13Horas_yaNoSale() = runTest {
        //Arrange
        coEvery { sonandoDataSource.listenSonando("yo") } returns flowOf(
            listOf(sonando("laura", "2026-11-12T23:00:00Z"))
        )
        coEvery { usuarioDataSource.listenSeguidos("yo") } returns flowOf(
            listOf(UsuarioPerfilFirestoreDto().copy(id = "laura"))
        )
        //Act
        val fila = repository.getSonandoLive(ahora = ahora).first()
        //Assert
        assertThat(fila).isEmpty()
    }

    @Test
    fun getSonandoLive_documento_mapeaItemReaccionesYHaceCuanto() = runTest {
        //Arrange
        coEvery { sonandoDataSource.listenSonando("yo") } returns flowOf(
            listOf(sonando("laura", "2026-11-13T09:00:00Z", reacciones = 4))
        )
        coEvery { usuarioDataSource.listenSeguidos("yo") } returns flowOf(
            listOf(UsuarioPerfilFirestoreDto().copy(id = "laura"))
        )
        //Act
        val laura = repository.getSonandoLive(ahora = ahora).first().first()
        //Assert
        assertThat(laura.nombre).isEqualTo("Nombre laura")
        assertThat(laura.item.titulo).isEqualTo("Album 1")
        assertThat(laura.reacciones).isEqualTo(4)
        assertThat(laura.timeAgo).isEqualTo("3 h")
    }

    @Test
    fun ponerSonando_sinSesion_sinSesionException() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser } returns null
        //Act
        val result = repository.ponerSonando(LocalPreviewProvider.fromZero)
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }

    @Test
    fun ponerSonando_dataSourceFalla_errorInesperado() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.photoUrl } returns null
        coEvery { authDataSource.currentUser?.email } returns "yo@example.com"
        coEvery { usuarioDataSource.getUsuarioById("yo", any()) } returns null
        coEvery { sonandoDataSource.ponerSonando(any(), any()) } throws RuntimeException("Fallo la escritura")
        //Act
        val result = repository.ponerSonando(LocalPreviewProvider.fromZero)
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(ErrorInesperadoException::class.java)
    }

    @Test
    fun reaccionar_aLoMio_noSePuede() = runTest {
        //Arrange
        val mio = sonando("yo", "2026-11-13T11:00:00Z").toSonandoUi(usuarioActualId = "yo", ahora = ahora)
        //Act
        val result = repository.reaccionar(mio)
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(SeguirseASiMismoException::class.java)
    }
}
