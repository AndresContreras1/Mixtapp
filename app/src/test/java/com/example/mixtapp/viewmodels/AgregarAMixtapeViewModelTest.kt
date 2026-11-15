package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.MixtapeRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.ui.screens.agregaramixtape.AgregarAMixtapeViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
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

// Agregar a una mixtape con los repositorios moqueados: mis mixtapes con cual ya tiene el item, el
// item de la ruta y que no se repita en una mixtape que ya lo tiene
@OptIn(ExperimentalCoroutinesApi::class)
class AgregarAMixtapeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var mixtapeRepository: MixtapeRepository
    private lateinit var viewModel: AgregarAMixtapeViewModel

    private val item = LocalPreviewProvider.fromZero
    private val sinElItem = LocalPreviewProvider.mixtape.copy(id = "m1", tieneElItem = false)
    private val conElItem = LocalPreviewProvider.mixtape.copy(id = "m2", nombre = "Para correr", tieneElItem = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catalogoRepository = mockk()
        mixtapeRepository = mockk()
        viewModel = AgregarAMixtapeViewModel(catalogoRepository, mixtapeRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // La pantalla pide el item de la ruta: mis mixtapes con su id y el item una vez
    private fun abrirConElItem() {
        every { mixtapeRepository.getMisMixtapesLive(item.id) } returns flowOf(listOf(sinElItem, conElItem))
        coEvery { catalogoRepository.getItem(TIPO_CANCION, item.id) } returns Result.success(item)
        viewModel.getItem(tipo = TIPO_CANCION, itemId = item.id)
    }

    @Test
    fun getItem_dosMixtapes_listaConCualYaLoTieneYElItem() = runTest {
        //Arrange
        abrirConElItem()
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.item?.id).isEqualTo(item.id)
        assertThat(state.mixtapes.map { it.tieneElItem }).containsExactly(false, true).inOrder()
    }

    @Test
    fun getItem_elItemFalla_suErrorNoTapaLasMixtapes() = runTest {
        //Arrange
        every { mixtapeRepository.getMisMixtapesLive(item.id) } returns flowOf(listOf(sinElItem))
        coEvery { catalogoRepository.getItem(TIPO_CANCION, item.id) } returns Result.failure(SinConexionException())
        //Act
        viewModel.getItem(tipo = TIPO_CANCION, itemId = item.id)
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.errorItemRes).isEqualTo(R.string.error_sin_conexion)
        assertThat(state.errorMessageRes).isNull()
        assertThat(state.mixtapes).hasSize(1)
    }

    @Test
    fun agregarA_mixtapeSinElItem_navega() = runTest {
        //Arrange
        abrirConElItem()
        coEvery { mixtapeRepository.agregarItem("m1", item) } returns Result.success(Unit)
        advanceUntilIdle()
        //Act
        viewModel.agregarA(mixtapeId = "m1")
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.navigate).isTrue()
    }

    @Test
    fun agregarA_mixtapeQueYaLoTiene_noLoRepiteNiNavega() = runTest {
        //Arrange: si se llamara al repositorio, navigate quedaria en true
        abrirConElItem()
        coEvery { mixtapeRepository.agregarItem(any(), any()) } returns Result.success(Unit)
        advanceUntilIdle()
        //Act
        viewModel.agregarA(mixtapeId = "m2")
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.navigate).isFalse()
    }

    @Test
    fun agregarA_mixtapeQueYaNoExiste_errorMixtapeNoEncontrada() = runTest {
        //Arrange
        abrirConElItem()
        coEvery { mixtapeRepository.agregarItem("m1", item) } returns Result.failure(ContenidoNoEncontradoException())
        advanceUntilIdle()
        //Act
        viewModel.agregarA(mixtapeId = "m1")
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.navigate).isFalse()
        assertThat(state.errorAccionRes).isEqualTo(R.string.mixtape_no_encontrada)
    }
}
