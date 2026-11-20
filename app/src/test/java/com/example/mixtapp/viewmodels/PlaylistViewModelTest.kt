package com.example.mixtapp.viewmodels

import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ReproductorRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.playlist.PlaylistViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

// La playlist con mocks: el catalogo llega una vez, mis likes por un flujo y cada fragmento termina
// enseguida, para ver que Reproducir todo sigue con la siguiente
@OptIn(ExperimentalCoroutinesApi::class)
class PlaylistViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var reproductorRepository: ReproductorRepository
    private lateinit var viewModel: PlaylistViewModel

    private fun cancion(id: String, likes: Int) = ItemConResenasUi(
        item = LocalPreviewProvider.emptinessMachine.copy(id = id, tipo = TIPO_CANCION, genero = "Metal", likes = likes),
        rating = 0.0, estrellas = 0, ratingCount = 0, recommendRate = 0, recomiendan = 0, yaCalificado = false,
        reviews = emptyList(),
    )

    private val catalogo = listOf(cancion("a", likes = 9), cancion("b", likes = 5))

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catalogoRepository = mockk()
        usuarioRepository = mockk()
        reproductorRepository = mockk()
        coEvery { catalogoRepository.getCatalogo() } returns Result.success(catalogo)
        every { usuarioRepository.getMisFavoritosLive() } returns flowOf(emptyList())
        viewModel = PlaylistViewModel(catalogoRepository, usuarioRepository, reproductorRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun abrirPlaylist_loMasEscuchado_lasCancionesEnOrden() = runTest {
        //Act
        viewModel.abrirPlaylist("lo_mas_escuchado")
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.playlist?.canciones?.map { it.id }).containsExactly("a", "b").inOrder()
    }

    @Test
    fun alternarReproducirTodo_cadaCancionTermina_suenanLasDosYSeDetiene() = runTest {
        //Arrange: cada fragmento llega a su final
        coEvery { reproductorRepository.getVistaPrevia(any()) } returns Result.success("https://preview.mp3")
        every { reproductorRepository.escuchar(any()) } returns
            flowOf(EstadoDeReproduccion.Sonando, EstadoDeReproduccion.Terminada)
        viewModel.abrirPlaylist("lo_mas_escuchado")
        advanceUntilIdle()
        //Act
        viewModel.alternarReproducirTodo()
        advanceUntilIdle()
        //Assert: pidio el fragmento de las dos y al final no suena nada
        coVerify(exactly = 2) { reproductorRepository.getVistaPrevia(any()) }
        assertThat(viewModel.uiState.value.reproduciendoTodo).isFalse()
        assertThat(viewModel.uiState.value.vistaPreviaId).isNull()
    }
}
