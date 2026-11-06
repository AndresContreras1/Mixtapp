package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.DueloRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.DueloDto
import com.example.mixtapp.data.dtos.ItemDelDueloDto
import com.example.mixtapp.data.dtos.UsuarioDto
import com.example.mixtapp.data.dtos.VotoDto
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.DueloUi
import com.example.mixtapp.data.model.OPCION_A
import com.example.mixtapp.data.model.OPCION_B
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.data.repository.DueloRepository
import com.example.mixtapp.data.repository.ErrorInesperadoException
import com.example.mixtapp.data.repository.SinSesionException
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

// El repositorio de los duelos con mocks: los flujos del data source se reemplazan por flowOf con lo
// que llegaria de Firestore, y se prueba como el repositorio los junta y los mapea
class DueloRepositoryTest {

    private val dueloDataSource = mockk<DueloRemoteDataSource>()
    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()

    private val repository = DueloRepository(dueloDataSource, usuarioDataSource, authDataSource)

    private val duelo = DueloUi(
        id = "dia_1_album",
        dia = 1,
        tipo = TIPO_ALBUM,
        genero = "Nu metal",
        itemA = LocalPreviewProvider.fromZero.toItemResumenUi(),
        itemB = LocalPreviewProvider.karolG.toItemResumenUi(),
        votosA = 0,
        votosB = 0,
        opiniones = emptyList(),
        miOpcion = null,
    )

    // Casi todas las pruebas son con la sesion iniciada: la que no, cambia el mock
    @Before
    fun setUp() {
        coEvery { authDataSource.currentUser?.uid } returns "yo"
    }

    @Test
    fun getDueloLive_nadieHaVotado_ceroVotosYSinItems() = runTest {
        //Arrange: el documento no existe todavia
        coEvery { dueloDataSource.listenDuelo("dia_1_album") } returns flowOf(null)
        coEvery { dueloDataSource.listenVotos("dia_1_album") } returns flowOf(emptyList())
        //Act
        val enVivo = repository.getDueloLive(dueloId = "dia_1_album", tipo = TIPO_ALBUM).first()
        //Assert
        assertThat(enVivo.votosA).isEqualTo(0)
        assertThat(enVivo.votosB).isEqualTo(0)
        assertThat(enVivo.itemA).isNull()
        assertThat(enVivo.miOpcion).isNull()
    }

    @Test
    fun getDueloLive_conDocumento_mapeaLosItemsConSuTipo() = runTest {
        //Arrange
        coEvery { dueloDataSource.listenDuelo("dia_1_album") } returns flowOf(
            DueloDto().copy(
                genero = "Metal",
                itemA = ItemDelDueloDto(id = "album_a", titulo = "A", subtitulo = "Banda A", imagenUrl = ""),
                itemB = ItemDelDueloDto(id = "album_b", titulo = "B", subtitulo = "Banda B", imagenUrl = ""),
                votosA = 3,
                votosB = 1,
            )
        )
        coEvery { dueloDataSource.listenVotos("dia_1_album") } returns flowOf(emptyList())
        //Act
        val enVivo = repository.getDueloLive(dueloId = "dia_1_album", tipo = TIPO_ALBUM).first()
        //Assert
        assertThat(enVivo.genero).isEqualTo("Metal")
        assertThat(enVivo.itemA?.id).isEqualTo("album_a")
        assertThat(enVivo.itemA?.tipo).isEqualTo(TIPO_ALBUM)
        assertThat(enVivo.votosA).isEqualTo(3)
    }

    @Test
    fun getDueloLive_miVotoEntreLosVotos_quedaMiOpcionYEsMio() = runTest {
        //Arrange: el id del voto es el uid de quien voto
        coEvery { dueloDataSource.listenDuelo("dia_1_album") } returns flowOf(null)
        coEvery { dueloDataSource.listenVotos("dia_1_album") } returns flowOf(
            listOf(
                VotoDto(id = "otro", opcion = OPCION_A, opinion = "Mejor A", usuario = UsuarioDto("otro", "Laura", null), createdAt = null),
                VotoDto(id = "yo", opcion = OPCION_B, opinion = "", usuario = UsuarioDto("yo", "Andres", null), createdAt = null),
            )
        )
        //Act
        val enVivo = repository.getDueloLive(dueloId = "dia_1_album", tipo = TIPO_ALBUM).first()
        //Assert
        assertThat(enVivo.miOpcion).isEqualTo(OPCION_B)
        assertThat(enVivo.opiniones.map { it.esMio }).containsExactly(false, true).inOrder()
        assertThat(enVivo.opiniones[0].autor).isEqualTo("Laura")
    }

    @Test
    fun votar_sinSesion_sinSesionException() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser } returns null
        //Act
        val result = repository.votar(duelo = duelo, opcion = OPCION_A, opinion = "")
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }

    @Test
    fun votar_conSesion_success() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.photoUrl } returns null
        coEvery { usuarioDataSource.getUsuarioById("yo", any()) } returns null
        coEvery { authDataSource.currentUser?.email } returns "yo@example.com"
        coEvery { dueloDataSource.votar(any(), any(), any(), any()) } returns Unit
        //Act
        val result = repository.votar(duelo = duelo, opcion = OPCION_A, opinion = "Mejor A")
        //Assert
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun votar_dataSourceFalla_errorInesperado() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.photoUrl } returns null
        coEvery { usuarioDataSource.getUsuarioById("yo", any()) } returns null
        coEvery { authDataSource.currentUser?.email } returns "yo@example.com"
        coEvery { dueloDataSource.votar(any(), any(), any(), any()) } throws RuntimeException("Fallo la transaccion")
        //Act
        val result = repository.votar(duelo = duelo, opcion = OPCION_B, opinion = "")
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(ErrorInesperadoException::class.java)
    }
}
