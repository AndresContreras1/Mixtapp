package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.datasource.LocationProvider
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.Coordenadas
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.ui.screens.review.WriteReviewViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

// Escribir resena con los repositorios moqueados: primero se carga el item, como lo hace la
// pantalla, y despues se publica
@OptIn(ExperimentalCoroutinesApi::class)
class WriteReviewViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val bogota = Coordenadas(latitud = 4.65, longitud = -74.06)
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var locationProvider: LocationProvider
    private lateinit var viewModel: WriteReviewViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catalogoRepository = mockk()
        reviewRepository = mockk()
        locationProvider = mockk()
        viewModel = WriteReviewViewModel(catalogoRepository, reviewRepository, locationProvider, testDispatcher)

        // El celular esta en Bogota
        coEvery { locationProvider.getCurrentLocation() } returns bogota

        // El album de ejemplo, sin resenas: el formulario arranca vacio
        coEvery { catalogoRepository.getItemConResenas(TIPO_ALBUM, "album_from_zero") } returns
            Result.success(LocalPreviewProvider.itemConResenas.copy(reviews = emptyList(), userRating = 0))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // Las tres pruebas empiezan con el formulario del album de ejemplo abierto
    private fun TestScope.abrirFormulario() {
        viewModel.getItem(TIPO_ALBUM, "album_from_zero")
        advanceUntilIdle()
    }

    @Test
    fun publicarResena_sinTexto_errorResenaVacia() = runTest {
        //Arrange
        abrirFormulario()
        viewModel.updateRating(4)
        //Act
        viewModel.publicarResena()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.navigate).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_resena_vacia)
    }

    @Test
    fun publicarResena_fechaConMes13_errorFechaInvalida() = runTest {
        //Arrange
        abrirFormulario()
        viewModel.updateReviewText("Muy bueno")
        viewModel.updateListenedDate("2026-13-01")
        //Act
        viewModel.publicarResena()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorMessageRes).isEqualTo(R.string.error_fecha_invalida)
    }

    @Test
    fun publicarResena_resenaValida_navega() = runTest {
        //Arrange
        abrirFormulario()
        viewModel.updateRating(5)
        viewModel.updateReviewText("Muy bueno")
        viewModel.updateListenedDate("2026-10-30")
        coEvery { reviewRepository.guardarResena(any(), any(), any(), any(), any(), any()) } returns Result.success(Unit)
        //Act
        viewModel.publicarResena()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.navigate).isTrue()
        assertThat(state.errorMessageRes).isNull()
    }

    @Test
    fun publicarResena_nueva_guardaDondeEstoy() = runTest {
        //Arrange: la resena nueva se publica con la ubicacion del celular
        abrirFormulario()
        viewModel.updateRating(5)
        viewModel.updateReviewText("Muy bueno")
        coEvery {
            reviewRepository.guardarResena(any(), any(), any(), any(), null, bogota)
        } returns Result.success(Unit)
        //Act
        viewModel.publicarResena()
        advanceUntilIdle()
        //Assert: solo navega si el repositorio recibio la ubicacion
        assertThat(viewModel.uiState.value.navigate).isTrue()
    }
}
