package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

// El resumen con los repositorios moqueados: mis reseñas, el catalogo, mis me gusta y mi perfil
// llegan una vez y se revisa lo que calcula el ViewModel
@OptIn(ExperimentalCoroutinesApi::class)
class ResumenViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var usuarioRepository: UsuarioRepository

    // El resumen es del año en curso: las reseñas de prueba se fechan este año
    private val anio = LocalDate.now().year

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        reviewRepository = mockk()
        catalogoRepository = mockk()
        usuarioRepository = mockk()
        coEvery { usuarioRepository.getMiPerfilLive() } returns flowOf(LocalPreviewProvider.usuario.copy(seguidores = 8, seguidos = 5))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Mi reseña de un item; el dia del mes sale de la calificacion solo para que cada una tenga
    // una fecha distinta
    private fun resena(id: String, itemId: String, rating: Int, likes: Int = 0, anioDeLaResena: Int = anio) =
        LocalPreviewProvider.reviews.first().copy(
            id = id,
            rating = rating,
            likes = likes,
            isMine = true,
            item = LocalPreviewProvider.reviews.first().item.copy(id = itemId),
            updatedAt = "$anioDeLaResena-10-1${rating}T00:00:00Z",
        )

    private fun album(id: String, genero: String, artista: String): ItemUi =
        LocalPreviewProvider.fromZero.copy(id = id, titulo = id, genero = genero, artista = artista)

    private fun like(id: String) = ItemResumenUi(id = id, tipo = TIPO_ALBUM, titulo = id, subtitulo = "", imagenUrl = "")

    // El ViewModel pide todo en su init: los mocks van antes de crearlo
    private fun crearViewModel(
        resenas: List<ReviewUi>,
        items: List<ItemUi>,
        favoritos: Flow<List<ItemResumenUi>> = flowOf(emptyList()),
    ): ResumenViewModel {
        coEvery { reviewRepository.getMyReviewsLive() } returns flowOf(resenas)
        coEvery { catalogoRepository.getItemsDelCatalogo() } returns Result.success(items)
        coEvery { usuarioRepository.getMisFavoritosLive() } returns favoritos
        return ResumenViewModel(reviewRepository, catalogoRepository, usuarioRepository, testDispatcher)
    }

    @Test
    fun init_tresResenas_totalYPromedioConUnDecimal() = runTest {
        //Arrange: 5, 4 y 4 estrellas
        val items = listOf(album("album_a", "Metal", "Metallica"), album("album_b", "Metal", "Metallica"), album("album_c", "Rock", "Queen"))
        val resenas = listOf(resena("r1", "album_a", 5), resena("r2", "album_b", 4), resena("r3", "album_c", 4))
        val viewModel = crearViewModel(resenas, items)
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.totalResenas).isEqualTo(3)
        assertThat(state.promedio).isEqualTo(4.3)
    }

    @Test
    fun init_resenaDelAnioPasado_noCuenta() = runTest {
        //Arrange: una de este año y otra del anterior
        val items = listOf(album("album_a", "Metal", "Metallica"), album("album_b", "Rock", "Queen"))
        val resenas = listOf(resena("r1", "album_a", 5), resena("r2", "album_b", 4, anioDeLaResena = anio - 1))
        val viewModel = crearViewModel(resenas, items)
        //Act
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.totalResenas).isEqualTo(1)
    }

    @Test
    fun init_meGustanMasMetal_generoYArtistaFavoritos() = runTest {
        //Arrange: dos me gusta de metal y una reseña buena de rock
        val items = listOf(album("album_a", "Metal", "Metallica"), album("album_b", "Metal", "Metallica"), album("album_c", "Rock", "Queen"))
        val viewModel = crearViewModel(
            resenas = listOf(resena("r1", "album_c", 4)),
            items = items,
            favoritos = flowOf(listOf(like("album_a"), like("album_b"))),
        )
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.generoFavorito).isEqualTo("Metal")
        assertThat(state.artistaFavorito).isEqualTo("Metallica")
        assertThat(state.totalMeGusta).isEqualTo(2)
    }

    @Test
    fun init_sinResenas_soloLaTarjetaDeResenasYLaSocial() = runTest {
        //Arrange
        val viewModel = crearViewModel(resenas = emptyList(), items = listOf(album("album_a", "Metal", "Metallica")))
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.totalResenas).isEqualTo(0)
        assertThat(state.tarjetas).containsExactly(TARJETA_RESENAS, TARJETA_SOCIAL).inOrder()
    }

    @Test
    fun init_resenasConLikes_mejorCalificada() = runTest {
        //Arrange
        val items = listOf(album("album_a", "Metal", "Metallica"), album("album_b", "Rock", "Queen"))
        val resenas = listOf(resena("r1", "album_a", 5, likes = 1), resena("r2", "album_b", 3, likes = 9))
        val viewModel = crearViewModel(resenas, items)
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.mejorCalificado?.id).isEqualTo("album_a")
        assertThat(state.mejorCalificacion).isEqualTo(5)
        assertThat(state.tarjetas).contains(TARJETA_MEJOR)
    }

    @Test
    fun init_resenasConLikes_laMasQuerida() = runTest {
        //Arrange
        val items = listOf(album("album_a", "Metal", "Metallica"), album("album_b", "Rock", "Queen"))
        val resenas = listOf(resena("r1", "album_a", 5, likes = 1), resena("r2", "album_b", 3, likes = 9))
        val viewModel = crearViewModel(resenas, items)
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.masQuerida?.id).isEqualTo("r2")
        assertThat(state.tarjetas).contains(TARJETA_QUERIDA)
    }

    @Test
    fun init_perfil_seguidoresYSeguidos() = runTest {
        //Arrange
        val viewModel = crearViewModel(resenas = emptyList(), items = emptyList())
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.seguidores).isEqualTo(8)
        assertThat(state.seguidos).isEqualTo(5)
    }

    @Test
    fun init_misResenasFallan_errorQueOcupaLaPantalla() = runTest {
        //Arrange
        coEvery { reviewRepository.getMyReviewsLive() } returns flow { throw SinConexionException() }
        coEvery { catalogoRepository.getItemsDelCatalogo() } returns Result.success(emptyList())
        coEvery { usuarioRepository.getMisFavoritosLive() } returns flowOf(emptyList())
        val viewModel = ResumenViewModel(reviewRepository, catalogoRepository, usuarioRepository, testDispatcher)
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_sin_conexion)
    }

    @Test
    fun init_misMeGustaFallan_avisaSinTaparElResumen() = runTest {
        //Arrange
        val viewModel = crearViewModel(
            resenas = emptyList(),
            items = emptyList(),
            favoritos = flow { throw SinConexionException() },
        )
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.errorMessageRes).isNull()
        assertThat(state.errorDatosRes).isEqualTo(R.string.error_sin_conexion)
    }

    @Test
    fun avanzar_hastaElFinal_noPasaDeLasTarjetas() = runTest {
        //Arrange: sin datos hay dos tarjetas
        val viewModel = crearViewModel(resenas = emptyList(), items = emptyList())
        advanceUntilIdle()
        //Act
        viewModel.avanzar()
        //Assert
        assertThat(viewModel.uiState.value.reveladas).isEqualTo(2)
        assertThat(viewModel.uiState.value.quedanPorVer).isFalse()
    }

    @Test
    fun avanzar_alFinal_vuelveALaPrimera() = runTest {
        //Arrange
        val viewModel = crearViewModel(resenas = emptyList(), items = emptyList())
        advanceUntilIdle()
        viewModel.avanzar()
        //Act
        viewModel.avanzar()
        //Assert
        assertThat(viewModel.uiState.value.reveladas).isEqualTo(1)
    }
}
