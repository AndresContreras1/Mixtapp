package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.repository.MixtapeRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.ui.screens.crearmixtape.CrearMixtapeViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

// Crear una mixtape con el repositorio moqueado: las validaciones del formulario y lo que pasa al
// guardar
@OptIn(ExperimentalCoroutinesApi::class)
class CrearMixtapeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mixtapeRepository: MixtapeRepository
    private lateinit var viewModel: CrearMixtapeViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mixtapeRepository = mockk()
        viewModel = CrearMixtapeViewModel(mixtapeRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun crearMixtape_sinNombre_errorNombreVacio() = runTest {
        //Arrange: solo espacios cuenta como vacio
        viewModel.updateNombre("   ")
        //Act
        viewModel.crearMixtape()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_nombre_mixtape_vacio)
        assertThat(state.navigate).isFalse()
    }

    @Test
    fun crearMixtape_nombreDe41Caracteres_errorNombreLargo() = runTest {
        //Arrange
        viewModel.updateNombre("a".repeat(41))
        //Act
        viewModel.crearMixtape()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorMessageRes).isEqualTo(R.string.error_nombre_mixtape_largo)
    }

    @Test
    fun crearMixtape_datosValidos_navegaSinError() = runTest {
        //Arrange
        viewModel.updateNombre("  Para entrenar  ")
        viewModel.updateDescripcion("Lo del gimnasio")
        coEvery { mixtapeRepository.crearMixtape("Para entrenar", "Lo del gimnasio") } returns Result.success("m1")
        //Act
        viewModel.crearMixtape()
        advanceUntilIdle()
        //Assert: el nombre se guarda sin los espacios de los lados
        val state = viewModel.uiState.value
        assertThat(state.navigate).isTrue()
        assertThat(state.errorMessageRes).isNull()
        assertThat(state.guardando).isFalse()
    }

    @Test
    fun crearMixtape_sinConexion_errorYNoNavega() = runTest {
        //Arrange
        viewModel.updateNombre("Para entrenar")
        coEvery { mixtapeRepository.crearMixtape(any(), any()) } returns Result.failure(SinConexionException())
        //Act
        viewModel.crearMixtape()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_sin_conexion)
        assertThat(state.navigate).isFalse()
    }
}
