package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.ComunidadRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.ChatDto
import com.example.mixtapp.data.dtos.ItemDeUsuarioDto
import com.example.mixtapp.data.dtos.UsuarioDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.UsuarioDelChatUi
import com.example.mixtapp.data.repository.ComunidadRepository
import com.example.mixtapp.data.repository.MensajeASiMismoException
import com.example.mixtapp.data.repository.SinSesionException
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

// La comunidad con mocks: los data sources se reemplazan y se revisa como el repositorio arma la
// afinidad y los chats y decide el error
class ComunidadRepositoryTest {

    private val comunidadDataSource = mockk<ComunidadRemoteDataSource>()
    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()

    private val repository = ComunidadRepository(comunidadDataSource, usuarioDataSource, authDataSource)

    private fun like(id: String, genero: String) = ItemDeUsuarioDto(id, TIPO_ALBUM, id, "", "", genero, null)

    private fun usuario(id: String) = UsuarioPerfilFirestoreDto().copy(id = id, nombre = "Nombre $id", username = id)

    @Test
    fun getAfinidades_unoCompartoTodo_primeroYSinMi() = runTest {
        //Arrange: "ana" tiene mis mismos likes y "beto" ninguno parecido
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { comunidadDataSource.getLikes("yo") } returns listOf(like("a", "Metal"), like("b", "Metal"))
        coEvery { comunidadDataSource.getLikes("ana") } returns listOf(like("a", "Metal"), like("b", "Metal"))
        coEvery { comunidadDataSource.getLikes("beto") } returns listOf(like("z", "Reggaetón"))
        coEvery { comunidadDataSource.getUsuarios(any()) } returns listOf(usuario("yo"), usuario("ana"), usuario("beto"))
        //Act
        val afinidades = repository.getAfinidades().getOrNull()
        //Assert: yo no salgo, beto no comparte nada y ana es la primera con 100
        assertThat(afinidades?.map { it.usuario.id }).containsExactly("ana")
        assertThat(afinidades?.first()?.porcentaje).isEqualTo(100)
        assertThat(afinidades?.first()?.generoEnComun).isEqualTo("Metal")
    }

    @Test
    fun getAfinidades_sinMisLikes_listaVacia() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { comunidadDataSource.getLikes("yo") } returns emptyList()
        //Act
        val afinidades = repository.getAfinidades().getOrNull()
        //Assert
        assertThat(afinidades).isEmpty()
    }

    @Test
    fun getAfinidades_sinSesion_sinSesion() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser } returns null
        //Act
        val result = repository.getAfinidades()
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }

    @Test
    fun getChatsLive_dosChats_elMasRecientePrimeroConLaOtraPersona() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        val yo = UsuarioDto(id = "yo", nombre = "Yo", fotoUrl = null)
        val viejo = ChatDto("ana_yo", listOf("ana", "yo"), listOf(yo, UsuarioDto("ana", "Ana", null)), "Hola", "2026-11-01T10:00:00Z")
        val nuevo = ChatDto("beto_yo", listOf("beto", "yo"), listOf(yo, UsuarioDto("beto", "Beto", null)), "Qué más", "2026-11-19T10:00:00Z")
        every { comunidadDataSource.listenChats("yo") } returns flowOf(listOf(viejo, nuevo))
        //Act
        val chats = repository.getChatsLive().first()
        //Assert
        assertThat(chats.map { it.otro.nombre }).containsExactly("Beto", "Ana").inOrder()
    }

    @Test
    fun enviarMensaje_aMiMismo_error() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        //Act
        val result = repository.enviarMensaje(otro = UsuarioDelChatUi("yo", "Yo", ""), texto = "Hola")
        //Assert
        assertThat(result.exceptionOrNull()).isInstanceOf(MensajeASiMismoException::class.java)
    }
}
