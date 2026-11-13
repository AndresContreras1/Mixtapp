package com.example.mixtapp.viewmodels

import com.example.mixtapp.data.datasource.LocationProvider
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.Coordenadas
import com.example.mixtapp.data.model.ResenaEnMapaUi
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.ui.screens.mapa.CENTRO_POR_DEFECTO
import com.example.mixtapp.ui.screens.mapa.MapaViewModel
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

// El mapa con el repositorio y la ubicacion moqueados: los marcadores que llegan, la tarjeta de la
// resena elegida y donde se centra
@OptIn(ExperimentalCoroutinesApi::class)
class MapaViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var locationProvider: LocationProvider
    private lateinit var viewModel: MapaViewModel

    private val medellin = Coordenadas(latitud = 6.24, longitud = -75.58)
    private val resenas = listOf(
        ResenaEnMapaUi(review = LocalPreviewProvider.review.copy(id = "r1"), latitud = 4.65, longitud = -74.06),
        ResenaEnMapaUi(review = LocalPreviewProvider.review.copy(id = "r2"), latitud = 6.24, longitud = -75.58),
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        reviewRepository = mockk()
        locationProvider = mockk()
        coEvery { reviewRepository.getResenasEnElMapaLive(any()) } returns flowOf(resenas)
        viewModel = MapaViewModel(reviewRepository, locationProvider, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun init_resenasDeLasUltimas24Horas_unMarcadorPorResena() = runTest {
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.resenas.map { it.review.id }).containsExactly("r1", "r2")
        assertThat(state.isLoading).isFalse()
        assertThat(state.seleccionada).isNull()
    }

    @Test
    fun seleccionar_unMarcador_muestraSuResena() = runTest {
        //Arrange
        advanceUntilIdle()
        //Act
        viewModel.seleccionar("r2")
        //Assert
        assertThat(viewModel.uiState.value.seleccionada?.review?.id).isEqualTo("r2")
    }

    @Test
    fun seleccionar_elMismoDosVeces_cierraLaTarjeta() = runTest {
        //Arrange
        advanceUntilIdle()
        viewModel.seleccionar("r2")
        //Act
        viewModel.seleccionar("r2")
        //Assert
        assertThat(viewModel.uiState.value.seleccionada).isNull()
    }

    @Test
    fun centrarEnMiUbicacion_conUbicacion_centraDondeEstoy() = runTest {
        //Arrange
        coEvery { locationProvider.getCurrentLocation() } returns medellin
        //Act
        viewModel.centrarEnMiUbicacion()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.centro).isEqualTo(medellin)
    }

    @Test
    fun centrarEnMiUbicacion_sinUbicacion_quedaBogota() = runTest {
        //Arrange: sin permiso o sin GPS el provider responde null
        coEvery { locationProvider.getCurrentLocation() } returns null
        //Act
        viewModel.centrarEnMiUbicacion()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.centro).isEqualTo(CENTRO_POR_DEFECTO)
    }
}
