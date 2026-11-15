package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.Coordenadas
import com.example.mixtapp.data.model.ResenaEnMapaUi
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.UbicacionNoDisponibleException
import com.example.mixtapp.data.repository.UbicacionRepository
import com.example.mixtapp.ui.screens.mapa.MapaViewModel
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

// El mapa con el repositorio y la ubicacion moqueados: los marcadores que llegan, la tarjeta de la
// resena elegida, donde se centra y los errores
@OptIn(ExperimentalCoroutinesApi::class)
class MapaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var ubicacionRepository: UbicacionRepository

    private val medellin = Coordenadas(latitud = 6.24, longitud = -75.58)
    private val resenas = listOf(
        ResenaEnMapaUi(review = LocalPreviewProvider.review.copy(id = "r1"), latitud = 4.65, longitud = -74.06),
        ResenaEnMapaUi(review = LocalPreviewProvider.review.copy(id = "r2"), latitud = 6.24, longitud = -75.58),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        reviewRepository = mockk()
        ubicacionRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // El ViewModel escucha las resenas en su init: el flujo se moquea antes de crearlo
    private fun crearViewModel(flujo: Flow<List<ResenaEnMapaUi>> = flowOf(resenas)): MapaViewModel {
        coEvery { reviewRepository.getResenasEnElMapaLive(any()) } returns flujo
        return MapaViewModel(reviewRepository, ubicacionRepository, testDispatcher)
    }

    @Test
    fun init_resenasDeLasUltimas24Horas_unMarcadorPorResena() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.resenas.map { it.review.id }).containsExactly("r1", "r2")
        assertThat(state.isLoading).isFalse()
        assertThat(state.seleccionada).isNull()
    }

    @Test
    fun init_llegaUnaResenaNueva_apareceSinVolverAEntrar() = runTest {
        //Arrange: el listener manda primero dos resenas y despues tres
        val nueva = ResenaEnMapaUi(review = LocalPreviewProvider.review.copy(id = "r3"), latitud = 3.45, longitud = -76.53)
        val viewModel = crearViewModel(flowOf(resenas, resenas + nueva))
        //Act
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.resenas.map { it.review.id }).containsExactly("r1", "r2", "r3")
    }

    @Test
    fun init_flujoFalla_errorYSinCarga() = runTest {
        //Arrange
        val viewModel = crearViewModel(flow { throw SinConexionException() })
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_sin_conexion)
    }

    @Test
    fun seleccionar_unMarcador_muestraSuResena() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        advanceUntilIdle()
        //Act
        viewModel.seleccionar("r2")
        //Assert
        assertThat(viewModel.uiState.value.seleccionada?.review?.id).isEqualTo("r2")
    }

    @Test
    fun seleccionar_elMismoDosVeces_cierraLaTarjeta() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        advanceUntilIdle()
        viewModel.seleccionar("r2")
        //Act
        viewModel.seleccionar("r2")
        //Assert
        assertThat(viewModel.uiState.value.seleccionada).isNull()
    }

    @Test
    fun sendOrDeleteLike_sinConexion_errorEnLaTarjeta() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        advanceUntilIdle()
        viewModel.seleccionar("r1")
        coEvery { reviewRepository.sendOrDeleteLike("r1") } returns Result.failure(SinConexionException())
        //Act
        viewModel.sendOrDeleteLike("r1")
        advanceUntilIdle()
        //Assert: el error va en la tarjeta y la resena sigue elegida
        val state = viewModel.uiState.value
        assertThat(state.errorAccionRes).isEqualTo(R.string.error_sin_conexion)
        assertThat(state.seleccionada?.review?.id).isEqualTo("r1")
    }

    @Test
    fun centrarEnMiUbicacion_conUbicacion_centraDondeEstoy() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        coEvery { ubicacionRepository.getUbicacionActual() } returns Result.success(medellin)
        //Act
        viewModel.centrarEnMiUbicacion()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.centro).isEqualTo(medellin)
    }

    @Test
    fun centrarEnMiUbicacion_sinUbicacion_elMapaNoSeMueve() = runTest {
        //Arrange: primero se centro en Medellin y despues el celular ya no da la ubicacion
        val viewModel = crearViewModel()
        coEvery { ubicacionRepository.getUbicacionActual() } returns Result.success(medellin)
        viewModel.centrarEnMiUbicacion()
        advanceUntilIdle()
        coEvery { ubicacionRepository.getUbicacionActual() } returns Result.failure(UbicacionNoDisponibleException())
        //Act
        viewModel.centrarEnMiUbicacion()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.centro).isEqualTo(medellin)
    }
}
