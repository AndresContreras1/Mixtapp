package com.example.mixtapp.viewmodels

import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.home.HomeViewModel
import com.example.mixtapp.ui.screens.home.model.FILTRO_DESCUBRE
import com.example.mixtapp.ui.screens.home.model.FILTRO_TENDENCIAS
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

// El inicio con los repositorios moqueados: el catalogo y mis likes llegan una vez y se revisa
// como reparte cada fila entre los generos que me gustan
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var reviewRepository: ReviewRepository

    // 10 canciones de metal y 10 de reggaeton; las de reggaeton son mas populares
    private val metal = (1..10).map { cancion(id = "metal_$it", genero = "Metal", likes = it) }
    private val reggaeton = (1..10).map { cancion(id = "reggaeton_$it", genero = "Reggaetón", likes = 100 + it) }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catalogoRepository = mockk()
        usuarioRepository = mockk()
        reviewRepository = mockk()
        coEvery { usuarioRepository.getMisSeguidosLive() } returns flowOf(emptyList())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun cancion(
        id: String,
        genero: String,
        likes: Int = 0,
        tendencia: Int? = null,
        tendenciaColombia: Int? = null,
        fecha: String = "2020-01-01",
    ) = ItemConResenasUi(
        item = LocalPreviewProvider.emptinessMachine.copy(
            id = id,
            genero = genero,
            subgenero = genero,
            likes = likes,
            tendencia = tendencia,
            tendenciaColombia = tendenciaColombia,
            fechaLanzamiento = fecha,
        ),
        rating = 0.0,
        estrellas = 0,
        ratingCount = 0,
        recommendRate = 0,
        userRating = 0,
        reviews = emptyList(),
    )

    // Un album con su puesto en la lista de su genero y en el Top Colombia
    private fun album(id: String, genero: String, tendencia: Int? = null, tendenciaColombia: Int? = null) =
        cancion(id = id, genero = genero, tendencia = tendencia, tendenciaColombia = tendenciaColombia).let {
            it.copy(item = it.item.copy(tipo = TIPO_ALBUM))
        }

    private fun like(id: String, genero: String) =
        ItemResumenUi(id = id, tipo = TIPO_CANCION, titulo = id, subtitulo = "", imagenUrl = "", genero = genero)

    private fun crearViewModel(catalogo: List<ItemConResenasUi>, favoritos: List<ItemResumenUi>): HomeViewModel {
        coEvery { catalogoRepository.getCatalogoLive() } returns flowOf(catalogo)
        coEvery { usuarioRepository.getMisFavoritosLive() } returns flowOf(favoritos)
        return HomeViewModel(catalogoRepository, usuarioRepository, reviewRepository, testDispatcher)
    }

    @Test
    fun init_casiTodoMetalYUnReggaeton_paraTiConSoloUnReggaeton() = runTest {
        //Arrange: 9 likes de metal y 1 de reggaeton
        val favoritos = metal.take(9).map { like(it.item.id, "Metal") } + like("reggaeton_1", "Reggaetón")
        val viewModel = crearViewModel(catalogo = metal + reggaeton, favoritos = favoritos)
        //Act
        advanceUntilIdle()
        //Assert: aunque el reggaeton es mas popular, de 10 canciones solo 1 es reggaeton
        val canciones = viewModel.uiState.value.canciones
        assertThat(canciones).hasSize(10)
        assertThat(canciones.count { it.genero == "Reggaetón" }).isEqualTo(1)
    }

    @Test
    fun updateSelectedFilter_descubreConMisLikes_primeroMiGeneroSinLoQueTieneMiLike() = runTest {
        //Arrange
        val favoritos = metal.take(3).map { like(it.item.id, "Metal") }
        val viewModel = crearViewModel(catalogo = metal + reggaeton, favoritos = favoritos)
        //Act
        viewModel.updateSelectedFilter(FILTRO_DESCUBRE)
        advanceUntilIdle()
        //Assert: las canciones que ya tienen mi like no salen. Primero van las 7 de metal que no
        // conozco y, como no alcanzan para los 10 cupos, la fila se completa con otro genero
        val canciones = viewModel.uiState.value.canciones
        assertThat(canciones.map { it.id }).containsNoneOf("metal_1", "metal_2", "metal_3")
        assertThat(canciones.take(7).map { it.genero }.toSet()).containsExactly("Metal")
        assertThat(canciones.drop(7).map { it.genero }.toSet()).containsExactly("Reggaetón")
    }

    @Test
    fun updateSelectedFilter_tendenciasMeGustaElMetal_primeroElTopColombia() = runTest {
        //Arrange: solo me gusta el metal, pero el Top Colombia es de reggaeton
        val catalogo = listOf(
            cancion(id = "reggaeton_top_2", genero = "Reggaetón", tendenciaColombia = 2),
            cancion(id = "reggaeton_top_1", genero = "Reggaetón", tendenciaColombia = 1),
            cancion(id = "metal_top_1", genero = "Metal", tendencia = 1),
        ) + metal
        val viewModel = crearViewModel(catalogo = catalogo, favoritos = listOf(like("metal_1", "Metal")))
        //Act
        viewModel.updateSelectedFilter(FILTRO_TENDENCIAS)
        advanceUntilIdle()
        //Assert: primero la lista de Colombia por su puesto, sin importar mis generos
        val canciones = viewModel.uiState.value.canciones
        assertThat(canciones.take(2).map { it.id }).containsExactly("reggaeton_top_1", "reggaeton_top_2").inOrder()
    }

    @Test
    fun updateSelectedFilter_tendencias_tarjetaEsElPrimeroDeColombia() = runTest {
        //Arrange
        val catalogo = listOf(
            album(id = "metal_album", genero = "Metal", tendencia = 1),
            album(id = "colombia_3", genero = "Reggaetón", tendenciaColombia = 3),
            album(id = "colombia_1", genero = "Música popular", tendenciaColombia = 1),
        )
        val viewModel = crearViewModel(catalogo = catalogo, favoritos = listOf(like("metal_album", "Metal")))
        //Act
        viewModel.updateSelectedFilter(FILTRO_TENDENCIAS)
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.enTendencia?.id).isEqualTo("colombia_1")
        assertThat(state.enTendenciaColombia).isTrue()
    }

    @Test
    fun init_albumesEnLaListaDeMiGenero_tarjetaEsUnoDeEllos() = runTest {
        //Arrange: me gusta el metal; hay tres albumes de metal en su lista y uno de reggaeton
        val delMetal = listOf("metal_a", "metal_b", "metal_c")
        val catalogo = delMetal.mapIndexed { i, id -> album(id = id, genero = "Metal", tendencia = i + 1) } +
            album(id = "reggaeton_a", genero = "Reggaetón", tendencia = 1)
        val viewModel = crearViewModel(catalogo = catalogo, favoritos = listOf(like("metal_a", "Metal")))
        //Act
        advanceUntilIdle()
        //Assert: sale al azar, pero siempre uno de metal que esta en su lista
        assertThat(viewModel.uiState.value.enTendencia?.id).isIn(delMetal)
    }

    @Test
    fun elegirAlbumEnTendencia_variasEntradas_siempreDeMiGeneroEnLista() = runTest {
        //Arrange
        val delMetal = listOf("metal_a", "metal_b", "metal_c")
        val catalogo = delMetal.mapIndexed { i, id -> album(id = id, genero = "Metal", tendencia = i + 1) } +
            album(id = "reggaeton_a", genero = "Reggaetón", tendencia = 1)
        val viewModel = crearViewModel(catalogo = catalogo, favoritos = listOf(like("metal_a", "Metal")))
        advanceUntilIdle()
        //Act: entra al inicio cinco veces
        val elegidos = (1..5).map {
            viewModel.elegirAlbumEnTendencia()
            viewModel.uiState.value.enTendencia?.id
        }
        //Assert
        assertThat(delMetal).containsAtLeastElementsIn(elegidos.toSet())
    }

    @Test
    fun init_sinAlbumesEnLaLista_tarjetaEsElMasPopular() = runTest {
        //Arrange: ningun album esta en una lista (como en los datos de las pruebas e2e)
        val catalogo = listOf(
            album(id = "album_1", genero = "Rock"),
            album(id = "album_2", genero = "Rock"),
        )
        val viewModel = crearViewModel(catalogo = catalogo, favoritos = emptyList())
        //Act
        advanceUntilIdle()
        //Assert: empatan en popularidad y queda el primero, siempre el mismo
        assertThat(viewModel.uiState.value.enTendencia?.id).isEqualTo("album_1")
    }
}
