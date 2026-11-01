package com.example.mixtapp.viewmodels

import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
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
        fecha: String = "2020-01-01",
    ) = ItemConResenasUi(
        item = LocalPreviewProvider.emptinessMachine.copy(
            id = id,
            genero = genero,
            subgenero = genero,
            likes = likes,
            tendencia = tendencia,
            fechaLanzamiento = fecha,
        ),
        rating = 0.0,
        estrellas = 0,
        ratingCount = 0,
        recommendRate = 0,
        userRating = 0,
        reviews = emptyList(),
    )

    private fun like(id: String, genero: String) =
        ItemResumenUi(id = id, tipo = TIPO_CANCION, titulo = id, subtitulo = "", imagenUrl = "", genero = genero)

    private fun crearViewModel(catalogo: List<ItemConResenasUi>, favoritos: List<ItemResumenUi>): HomeViewModel {
        coEvery { catalogoRepository.getCatalogoLive() } returns flowOf(catalogo)
        coEvery { usuarioRepository.getMisFavoritosLive() } returns flowOf(favoritos)
        return HomeViewModel(catalogoRepository, usuarioRepository, reviewRepository, testDispatcher)
    }

    @Test
    fun paraTi_casiTodoMetalYUnReggaeton_soloUnReggaeton() = runTest {
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
    fun descubre_conMisLikes_soloLoQueNoTieneMiLike() = runTest {
        //Arrange
        val favoritos = metal.take(3).map { like(it.item.id, "Metal") }
        val viewModel = crearViewModel(catalogo = metal + reggaeton, favoritos = favoritos)
        //Act
        viewModel.updateSelectedFilter(FILTRO_DESCUBRE)
        advanceUntilIdle()
        //Assert: las canciones que ya tienen mi like no salen, y todo es de mi genero
        val canciones = viewModel.uiState.value.canciones
        assertThat(canciones.map { it.id }).containsNoneOf("metal_1", "metal_2", "metal_3")
        assertThat(canciones.map { it.genero }.toSet()).containsExactly("Metal")
    }

    @Test
    fun tendencias_meGustaElMetal_primeroLaListaDelMetal() = runTest {
        //Arrange: los dos generos tienen canciones en su lista; solo me gusta el metal
        val catalogo = listOf(
            cancion(id = "metal_top_2", genero = "Metal", tendencia = 2),
            cancion(id = "metal_top_1", genero = "Metal", tendencia = 1),
            cancion(id = "reggaeton_top_1", genero = "Reggaetón", tendencia = 1),
        ) + metal
        val viewModel = crearViewModel(catalogo = catalogo, favoritos = listOf(like("metal_1", "Metal")))
        //Act
        viewModel.updateSelectedFilter(FILTRO_TENDENCIAS)
        advanceUntilIdle()
        //Assert: primero las de la lista del metal por su puesto, y nada de reggaeton
        val canciones = viewModel.uiState.value.canciones
        assertThat(canciones.take(2).map { it.id }).containsExactly("metal_top_1", "metal_top_2").inOrder()
        assertThat(canciones.map { it.genero }).doesNotContain("Reggaetón")
    }
}
