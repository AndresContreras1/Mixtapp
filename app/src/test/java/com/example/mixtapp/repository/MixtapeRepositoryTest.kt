package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorInesperadoException
import com.example.mixtapp.data.repository.MixtapeRepository
import com.example.mixtapp.data.repository.SinSesionException
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

// Las mixtapes con mocks: los flujos del data source se reemplazan por flowOf con lo que llegaria
// de Firestore, y se prueba como el repositorio los mapea y decide los errores
class MixtapeRepositoryTest {

    private val mixtapeDataSource = mockk<MixtapeRemoteDataSource>()
    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()

    private val repository = MixtapeRepository(mixtapeDataSource, usuarioDataSource, authDataSource)

    private val numb = ItemMixtapeDto(id = "cancion_numb", tipo = "cancion", titulo = "Numb", subtitulo = "Meteora", imagenUrl = "", genero = "Nu metal")

    // Casi todas las pruebas son con la sesion iniciada: la que no, cambia el mock
    @Before
    fun setUp() {
        coEvery { authDataSource.currentUser?.uid } returns "yo"
    }

    @Test
    fun getMisMixtapesLive_dosMixtapes_mapeaItemsYDuenio() = runTest {
        //Arrange
        coEvery { mixtapeDataSource.listenMixtapesDeUsuario("yo") } returns flowOf(
            listOf(
                MixtapeDto().copy(id = "m1", nombre = "Para entrenar", usuarioId = "yo", items = listOf(numb)),
                MixtapeDto().copy(id = "m2", nombre = "Vacia", usuarioId = "yo", descripcion = null),
            )
        )
        //Act
        val mixtapes = repository.getMisMixtapesLive().first()
        //Assert
        assertThat(mixtapes.map { it.nombre }).containsExactly("Para entrenar", "Vacia").inOrder()
        assertThat(mixtapes[0].items.first().titulo).isEqualTo("Numb")
        assertThat(mixtapes[0].itemCount).isEqualTo(1)
        assertThat(mixtapes[0].isMine).isTrue()
    }

    @Test
    fun getMixtapeLive_sinDescripcion_quedaVacia() = runTest {
        //Arrange
        coEvery { mixtapeDataSource.listenMixtapeById("m1") } returns flowOf(
            MixtapeDto().copy(id = "m1", nombre = "Para entrenar", usuarioId = "otro", descripcion = null)
        )
        //Act
        val mixtape = repository.getMixtapeLive("m1").first()
        //Assert
        assertThat(mixtape.descripcion).isEmpty()
        assertThat(mixtape.isMine).isFalse()
    }

    @Test
    fun getMixtapeLive_queNoExiste_contenidoNoEncontrado() = runTest {
        //Arrange: el data source envia null cuando la mixtape no existe
        coEvery { mixtapeDataSource.listenMixtapeById("m999") } returns flowOf(null)
        //Act
        val error = runCatching { repository.getMixtapeLive("m999").first() }.exceptionOrNull()
        //Assert
        assertThat(error).isInstanceOf(ContenidoNoEncontradoException::class.java)
    }

    @Test
    fun crearMixtape_sinSesion_sinSesionException() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser } returns null
        //Act
        val result = repository.crearMixtape(nombre = "Para entrenar", descripcion = "")
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }

    @Test
    fun crearMixtape_conSesion_retornaElId() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.photoUrl } returns null
        coEvery { authDataSource.currentUser?.email } returns "yo@example.com"
        coEvery { usuarioDataSource.getUsuarioById("yo", any()) } returns null
        coEvery { mixtapeDataSource.crearMixtape(any()) } returns "m_nueva"
        //Act
        val result = repository.crearMixtape(nombre = "Para entrenar", descripcion = "")
        //Assert
        assertThat(result.getOrNull()).isEqualTo("m_nueva")
    }

    @Test
    fun agregarItem_dataSourceFalla_errorInesperado() = runTest {
        //Arrange
        coEvery { mixtapeDataSource.agregarItem(any(), any()) } throws RuntimeException("Fallo la transaccion")
        //Act
        val result = repository.agregarItem(mixtapeId = "m1", item = LocalPreviewProvider.fromZero)
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(ErrorInesperadoException::class.java)
    }
}
