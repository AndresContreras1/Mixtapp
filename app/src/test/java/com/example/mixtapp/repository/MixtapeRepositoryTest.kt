package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import com.example.mixtapp.data.dtos.UsuarioDto
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
// de Firestore, y se prueba como el repositorio los ordena, los mapea y decide los errores
class MixtapeRepositoryTest {

    private val mixtapeDataSource = mockk<MixtapeRemoteDataSource>()
    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()

    private val repository = MixtapeRepository(mixtapeDataSource, usuarioDataSource, authDataSource)

    private fun item(id: String, titulo: String, agregadoEn: String) =
        ItemMixtapeDto(id = id, tipo = "cancion", titulo = titulo, subtitulo = "Meteora", imagenUrl = "", agregadoEn = agregadoEn)

    // Casi todas las pruebas son con la sesion iniciada: las que no, cambian el mock
    @Before
    fun setUp() {
        coEvery { authDataSource.currentUser?.uid } returns "yo"
    }

    @Test
    fun getMisMixtapesLive_dosMixtapes_laMasNuevaPrimeroConSuResumen() = runTest {
        //Arrange: el data source las entrega sin orden
        coEvery { mixtapeDataSource.listenMixtapesDeUsuario("yo", "") } returns flowOf(
            listOf(
                MixtapeDto().copy(id = "m2", nombre = "Vacia", usuarioId = "yo", createdAt = "2026-11-01T00:00:00Z"),
                MixtapeDto().copy(
                    id = "m1", nombre = "Para entrenar", usuarioId = "yo", itemsCount = 1,
                    portadas = listOf("numb.jpg"), createdAt = "2026-11-10T00:00:00Z",
                ),
            )
        )
        //Act
        val mixtapes = repository.getMisMixtapesLive().first()
        //Assert
        assertThat(mixtapes.map { it.nombre }).containsExactly("Para entrenar", "Vacia").inOrder()
        assertThat(mixtapes[0].itemsCount).isEqualTo(1)
        assertThat(mixtapes[0].portadas).containsExactly("numb.jpg")
        assertThat(mixtapes[0].isMine).isTrue()
    }

    @Test
    fun getMisMixtapesLive_conItem_diceEnCualesYaEsta() = runTest {
        //Arrange: el data source marca la que ya tiene el item
        coEvery { mixtapeDataSource.listenMixtapesDeUsuario("yo", "cancion_numb") } returns flowOf(
            listOf(MixtapeDto().copy(id = "m1", usuarioId = "yo", tieneElItem = true))
        )
        //Act
        val mixtapes = repository.getMisMixtapesLive(itemId = "cancion_numb").first()
        //Assert
        assertThat(mixtapes.single().tieneElItem).isTrue()
    }

    @Test
    fun getMixtapeLive_conItems_enElOrdenEnQueSeAgregaronYConSuCreador() = runTest {
        //Arrange
        coEvery { mixtapeDataSource.listenMixtapeById("m1") } returns flowOf(
            MixtapeDto().copy(
                id = "m1", nombre = "Para entrenar", usuarioId = "otro", descripcion = null,
                usuario = UsuarioDto(id = "", nombre = "Laura", fotoUrl = null),
            )
        )
        coEvery { mixtapeDataSource.listenItemsDeMixtape("m1") } returns flowOf(
            listOf(
                item("cancion_2", "In the End", "2026-11-02T00:00:00Z"),
                item("cancion_1", "Numb", "2026-11-01T00:00:00Z"),
            )
        )
        //Act
        val mixtapeConItems = repository.getMixtapeLive("m1").first()
        //Assert
        assertThat(mixtapeConItems.items.map { it.titulo }).containsExactly("Numb", "In the End").inOrder()
        assertThat(mixtapeConItems.mixtape.usuarioNombre).isEqualTo("Laura")
        assertThat(mixtapeConItems.mixtape.descripcion).isEmpty()
        assertThat(mixtapeConItems.mixtape.isMine).isFalse()
    }

    @Test
    fun getMixtapeLive_queNoExiste_contenidoNoEncontrado() = runTest {
        //Arrange: el data source envia null cuando la mixtape no existe
        coEvery { mixtapeDataSource.listenMixtapeById("m999") } returns flowOf(null)
        coEvery { mixtapeDataSource.listenItemsDeMixtape("m999") } returns flowOf(emptyList())
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
    fun agregarItem_mixtapeQueYaNoExiste_contenidoNoEncontrado() = runTest {
        //Arrange: la borraron mientras se elegia y el data source responde false
        coEvery { mixtapeDataSource.agregarItem("m1", LocalPreviewProvider.fromZero.id, any()) } returns false
        //Act
        val result = repository.agregarItem(mixtapeId = "m1", item = LocalPreviewProvider.fromZero)
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(ContenidoNoEncontradoException::class.java)
    }

    @Test
    fun agregarItem_mixtapeExistente_success() = runTest {
        //Arrange
        coEvery { mixtapeDataSource.agregarItem("m1", LocalPreviewProvider.fromZero.id, any()) } returns true
        //Act
        val result = repository.agregarItem(mixtapeId = "m1", item = LocalPreviewProvider.fromZero)
        //Assert
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun agregarItem_dataSourceFalla_errorInesperado() = runTest {
        //Arrange
        coEvery { mixtapeDataSource.agregarItem(any(), any(), any()) } throws RuntimeException("Fallo la transaccion")
        //Act
        val result = repository.agregarItem(mixtapeId = "m1", item = LocalPreviewProvider.fromZero)
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(ErrorInesperadoException::class.java)
    }

    @Test
    fun quitarItem_mixtapeExistente_success() = runTest {
        //Arrange
        coEvery { mixtapeDataSource.quitarItem("m1", "cancion_numb") } returns true
        //Act
        val result = repository.quitarItem(mixtapeId = "m1", itemId = "cancion_numb")
        //Assert
        assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun quitarItem_sinSesion_sinSesionException() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser } returns null
        //Act
        val result = repository.quitarItem(mixtapeId = "m1", itemId = "cancion_numb")
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }

    @Test
    fun eliminarMixtape_conSesion_success() = runTest {
        //Arrange
        coEvery { mixtapeDataSource.eliminarMixtape("m1") } returns Unit
        //Act
        val result = repository.eliminarMixtape(mixtapeId = "m1")
        //Assert
        assertThat(result.isSuccess).isTrue()
    }
}
