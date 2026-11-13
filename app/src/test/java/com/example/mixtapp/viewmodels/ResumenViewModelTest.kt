package com.example.mixtapp.viewmodels

import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.resumen.ResumenViewModel
import com.example.mixtapp.ui.screens.resumen.TARJETA_MEJOR
import com.example.mixtapp.ui.screens.resumen.TARJETA_QUERIDA
import com.example.mixtapp.ui.screens.resumen.TARJETA_RESENAS
import com.example.mixtapp.ui.screens.resumen.TARJETA_SOCIAL
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
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

// El resumen con los repositorios moqueados: el catalogo (con mis reseñas), mis me gusta y mi
// perfil llegan una vez y se revisa lo que calcula el ViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ResumenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var usuarioRepository: UsuarioRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catalogoRepository = mockk()
        usuarioRepository = mockk()
        coEvery { usuarioRepository.getMiPerfilLive() } returns flowOf(LocalPreviewProvider.usuario.copy(seguidores = 8, seguidos = 5))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun resena(id: String, rating: Int, likes: Int = 0, mia: Boolean = true) =
        LocalPreviewProvider.reviews.first().copy(id = id, rating = rating, likes = likes, isMine = mia, updatedAt = "2026-10-0${rating}T00:00:00Z")

    private fun album(id: String, genero: String, artista: String, reviews: List<ReviewUi>) = ItemConResenasUi(
        item = LocalPreviewProvider.fromZero.copy(id = id, titulo = id, genero = genero, artista = artista),
        rating = 0.0,
        estrellas = 0,
        ratingCount = reviews.size,
        recommendRate = 0,
        userRating = 0,
        reviews = reviews,
    )

    private fun like(id: String) = ItemResumenUi(id = id, tipo = TIPO_ALBUM, titulo = id, subtitulo = "", imagenUrl = "")

    private fun crearViewModel(catalogo: List<ItemConResenasUi>, favoritos: List<ItemResumenUi> = emptyList()): ResumenViewModel {
        coEvery { catalogoRepository.getCatalogoLive() } returns flowOf(catalogo)
        coEvery { usuarioRepository.getMisFavoritosLive() } returns flowOf(favoritos)
        return ResumenViewModel(catalogoRepository, usuarioRepository, testDispatcher)
    }

    @Test
    fun init_tresResenas_totalYPromedioConUnDecimal() = runTest {
        //Arrange: 5, 4 y 4 estrellas
        val catalogo = listOf(
            album("album_a", "Metal", "Metallica", listOf(resena("r1", 5))),
            album("album_b", "Metal", "Metallica", listOf(resena("r2", 4))),
            album("album_c", "Rock", "Queen", listOf(resena("r3", 4))),
        )
        val viewModel = crearViewModel(catalogo)
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.totalResenas).isEqualTo(3)
        assertThat(state.promedio).isEqualTo(4.3)
    }

    @Test
    fun init_meGustanMasMetal_generoYArtistaFavoritos() = runTest {
        //Arrange: dos me gusta de metal y una reseña buena de rock
        val catalogo = listOf(
            album("album_a", "Metal", "Metallica", emptyList()),
            album("album_b", "Metal", "Metallica", emptyList()),
            album("album_c", "Rock", "Queen", listOf(resena("r1", 4))),
        )
        val viewModel = crearViewModel(catalogo, favoritos = listOf(like("album_a"), like("album_b")))
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.generoFavorito).isEqualTo("Metal")
        assertThat(state.artistaFavorito).isEqualTo("Metallica")
        assertThat(state.totalMeGusta).isEqualTo(2)
    }

    @Test
    fun init_resenasDeOtros_noCuentan() = runTest {
        //Arrange: la unica reseña es de otro usuario
        val catalogo = listOf(album("album_a", "Metal", "Metallica", listOf(resena("r1", 5, mia = false))))
        val viewModel = crearViewModel(catalogo)
        //Act
        advanceUntilIdle()
        //Assert: solo quedan la tarjeta de reseñas (en 0) y la social
        val state = viewModel.uiState.value
        assertThat(state.totalResenas).isEqualTo(0)
        assertThat(state.tarjetas).containsExactly(TARJETA_RESENAS, TARJETA_SOCIAL).inOrder()
    }

    @Test
    fun init_resenasConLikes_mejorCalificadaYLaMasQuerida() = runTest {
        //Arrange
        val catalogo = listOf(
            album("album_a", "Metal", "Metallica", listOf(resena("r1", 5, likes = 1))),
            album("album_b", "Rock", "Queen", listOf(resena("r2", 3, likes = 9))),
        )
        val viewModel = crearViewModel(catalogo)
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.mejorCalificado?.id).isEqualTo("album_a")
        assertThat(state.mejorCalificacion).isEqualTo(5)
        assertThat(state.masQuerida?.id).isEqualTo("r2")
        assertThat(state.tarjetas).containsAtLeast(TARJETA_MEJOR, TARJETA_QUERIDA)
    }

    @Test
    fun init_perfil_seguidoresYSeguidos() = runTest {
        //Arrange
        val viewModel = crearViewModel(catalogo = emptyList())
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.seguidores).isEqualTo(8)
        assertThat(state.seguidos).isEqualTo(5)
    }

    @Test
    fun mostrarSiguiente_hastaElFinal_noPasaDeLasTarjetas() = runTest {
        //Arrange: sin datos hay dos tarjetas
        val viewModel = crearViewModel(catalogo = emptyList())
        advanceUntilIdle()
        //Act
        viewModel.mostrarSiguiente()
        viewModel.mostrarSiguiente()
        viewModel.mostrarSiguiente()
        //Assert
        assertThat(viewModel.uiState.value.reveladas).isEqualTo(2)
    }

    @Test
    fun verDeNuevo_alFinal_vuelveALaPrimera() = runTest {
        //Arrange
        val viewModel = crearViewModel(catalogo = emptyList())
        advanceUntilIdle()
        viewModel.mostrarSiguiente()
        //Act
        viewModel.verDeNuevo()
        //Assert
        assertThat(viewModel.uiState.value.reveladas).isEqualTo(1)
    }
}
