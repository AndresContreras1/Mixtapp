package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.DueloEnVivoUi
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.OPCION_A
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.DueloRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.ui.screens.duelos.DuelosViewModel
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

// Los duelos con los repositorios moqueados: el catalogo y los votos llegan una vez y se revisa el
// duelo que arma el ViewModel y lo que pasa al votar. El dia es el de hoy, asi que se revisa lo que
// vale para cualquier dia
@OptIn(ExperimentalCoroutinesApi::class)
class DuelosViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var dueloRepository: DueloRepository

    // Albumes de dos generos y canciones de uno; no hay artistas
    private val catalogo = listOf(
        item(id = "album_m1", tipo = TIPO_ALBUM, genero = "Metal"),
        item(id = "album_m2", tipo = TIPO_ALBUM, genero = "Metal"),
        item(id = "album_m3", tipo = TIPO_ALBUM, genero = "Metal"),
        item(id = "album_r1", tipo = TIPO_ALBUM, genero = "Rock"),
        item(id = "album_r2", tipo = TIPO_ALBUM, genero = "Rock"),
        item(id = "cancion_r1", tipo = TIPO_CANCION, genero = "Rock"),
        item(id = "cancion_r2", tipo = TIPO_CANCION, genero = "Rock"),
    )

    private val sinVotos = DueloEnVivoUi(
        genero = null,
        votosA = 0,
        votosB = 0,
        itemA = null,
        itemB = null,
        opiniones = emptyList(),
        miOpcion = null,
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catalogoRepository = mockk()
        dueloRepository = mockk()
        coEvery { catalogoRepository.getCatalogoLive() } returns flowOf(catalogo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun item(id: String, tipo: String, genero: String) = ItemConResenasUi(
        item = LocalPreviewProvider.fromZero.copy(id = id, tipo = tipo, genero = genero, titulo = id),
        rating = 0.0,
        estrellas = 0,
        ratingCount = 0,
        recommendRate = 0,
        userRating = 0,
        reviews = emptyList(),
    )

    private fun crearViewModel(enVivo: DueloEnVivoUi = sinVotos): DuelosViewModel {
        coEvery { dueloRepository.getDueloLive(any(), any()) } returns flowOf(enVivo)
        return DuelosViewModel(catalogoRepository, dueloRepository, testDispatcher)
    }

    @Test
    fun init_nadieHaVotado_dosDistintosDelMismoGeneroYTipo() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        //Act
        advanceUntilIdle()
        //Assert
        val duelo = viewModel.uiState.value.duelos[TIPO_ALBUM]
        val generoDe = catalogo.associate { it.item.id to it.item.genero }
        assertThat(duelo).isNotNull()
        assertThat(duelo?.itemA?.id).isNotEqualTo(duelo?.itemB?.id)
        assertThat(generoDe[duelo?.itemA?.id]).isEqualTo(duelo?.genero)
        assertThat(generoDe[duelo?.itemB?.id]).isEqualTo(duelo?.genero)
        assertThat(duelo?.itemA?.tipo).isEqualTo(TIPO_ALBUM)
    }

    @Test
    fun init_tipoSinDosDelMismoGenero_noHayDuelo() = runTest {
        //Arrange: el catalogo no tiene artistas
        val viewModel = crearViewModel()
        //Act
        advanceUntilIdle()
        //Assert
        val duelos = viewModel.uiState.value.duelos
        assertThat(duelos.keys).containsExactly(TIPO_ALBUM, TIPO_CANCION)
        assertThat(duelos[TIPO_ARTISTA]).isNull()
    }

    @Test
    fun init_alguienYaVoto_losItemsSalenDelDocumento() = runTest {
        //Arrange: el duelo de hoy ya existe con dos items que no estan en el catalogo
        val itemA = ItemResumenUi(id = "album_x", tipo = TIPO_ALBUM, titulo = "X", subtitulo = "", imagenUrl = "")
        val itemB = ItemResumenUi(id = "album_y", tipo = TIPO_ALBUM, titulo = "Y", subtitulo = "", imagenUrl = "")
        val viewModel = crearViewModel(
            enVivo = sinVotos.copy(genero = "Jazz", itemA = itemA, itemB = itemB, votosA = 2, votosB = 1, miOpcion = OPCION_A)
        )
        //Act
        advanceUntilIdle()
        //Assert
        val duelo = viewModel.uiState.value.duelos[TIPO_ALBUM]
        assertThat(duelo?.itemA?.id).isEqualTo("album_x")
        assertThat(duelo?.genero).isEqualTo("Jazz")
        assertThat(duelo?.porcentajeA).isEqualTo(66)
        assertThat(duelo?.miOpcion).isEqualTo(OPCION_A)
    }

    @Test
    fun votar_sinElegirNiHaberVotado_errorEligeUno() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        advanceUntilIdle()
        //Act
        viewModel.votar()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorVotoRes).isEqualTo(R.string.error_elige_uno)
    }

    @Test
    fun votar_elegidoConOpinion_limpiaElFormulario() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        advanceUntilIdle()
        viewModel.elegirOpcion(OPCION_A)
        viewModel.updateOpinion("El mejor del genero")
        coEvery { dueloRepository.votar(any(), OPCION_A, "El mejor del genero") } returns Result.success(Unit)
        //Act
        viewModel.votar()
        advanceUntilIdle()
        //Assert: los votos cambian por el listener; aqui solo se limpia el formulario
        val state = viewModel.uiState.value
        assertThat(state.opcionElegida).isNull()
        assertThat(state.opinion).isEmpty()
        assertThat(state.enviando).isFalse()
        assertThat(state.errorVotoRes).isNull()
    }

    @Test
    fun votar_sinConexion_errorYConservaLaOpinion() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        advanceUntilIdle()
        viewModel.elegirOpcion(OPCION_A)
        viewModel.updateOpinion("Mi opinion")
        coEvery { dueloRepository.votar(any(), any(), any()) } returns Result.failure(SinConexionException())
        //Act
        viewModel.votar()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.errorVotoRes).isEqualTo(R.string.error_sin_conexion)
        assertThat(state.opinion).isEqualTo("Mi opinion")
    }

    @Test
    fun updateOpinion_masDe140Caracteres_seCorta() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        //Act
        viewModel.updateOpinion("a".repeat(200))
        //Assert
        assertThat(viewModel.uiState.value.opinion).hasLength(140)
    }

    @Test
    fun selectTipo_otraPestana_limpiaElFormulario() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        advanceUntilIdle()
        viewModel.elegirOpcion(OPCION_A)
        viewModel.updateOpinion("Algo")
        //Act
        viewModel.selectTipo(TIPO_CANCION)
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.tipoElegido).isEqualTo(TIPO_CANCION)
        assertThat(state.opcionElegida).isNull()
        assertThat(state.opinion).isEmpty()
    }
}
