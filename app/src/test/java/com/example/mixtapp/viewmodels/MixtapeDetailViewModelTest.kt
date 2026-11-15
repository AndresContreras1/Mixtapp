package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.MixtapeConItemsUi
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorInesperadoException
import com.example.mixtapp.data.repository.MixtapeRepository
import com.example.mixtapp.ui.screens.mixtapedetail.MixtapeDetailViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

// El detalle de una mixtape con el repositorio moqueado: la mixtape con sus items en vivo, quitar un
// item, borrarla y sus errores
@OptIn(ExperimentalCoroutinesApi::class)
class MixtapeDetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var mixtapeRepository: MixtapeRepository
    private lateinit var viewModel: MixtapeDetailViewModel

    private val mixtapeConItems: MixtapeConItemsUi = LocalPreviewProvider.mixtapeConItems
    private val mixtapeId = mixtapeConItems.mixtape.id

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mixtapeRepository = mockk()
        viewModel = MixtapeDetailViewModel(mixtapeRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getMixtape_conItems_llegaSinCarga() = runTest {
        //Arrange
        every { mixtapeRepository.getMixtapeLive(mixtapeId) } returns flowOf(mixtapeConItems)
        //Act
        viewModel.getMixtape(id = mixtapeId)
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.mixtapeConItems?.items).hasSize(2)
    }

    @Test
    fun getMixtape_queNoExiste_errorMixtapeNoEncontrada() = runTest {
        //Arrange
        every { mixtapeRepository.getMixtapeLive(mixtapeId) } returns flow { throw ContenidoNoEncontradoException() }
        //Act
        viewModel.getMixtape(id = mixtapeId)
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.mixtape_no_encontrada)
    }

    @Test
    fun quitarItem_falla_errorDeLaAccionSinTaparLaMixtape() = runTest {
        //Arrange
        every { mixtapeRepository.getMixtapeLive(mixtapeId) } returns flowOf(mixtapeConItems)
        coEvery { mixtapeRepository.quitarItem(mixtapeId, "item_1") } returns Result.failure(ErrorInesperadoException())
        viewModel.getMixtape(id = mixtapeId)
        advanceUntilIdle()
        //Act
        viewModel.quitarItem(itemId = "item_1")
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.errorAccionRes).isEqualTo(R.string.error_mixtape)
        assertThat(state.mixtapeConItems).isNotNull()
    }

    @Test
    fun eliminarMixtape_funciona_quedaEliminada() = runTest {
        //Arrange
        every { mixtapeRepository.getMixtapeLive(mixtapeId) } returns flowOf(mixtapeConItems)
        coEvery { mixtapeRepository.eliminarMixtape(mixtapeId) } returns Result.success(Unit)
        viewModel.getMixtape(id = mixtapeId)
        advanceUntilIdle()
        //Act
        viewModel.eliminarMixtape()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.eliminada).isTrue()
    }

    @Test
    fun eliminarMixtape_antesDeQueLlegue_noHaceNada() = runTest {
        //Arrange: sin mixtape cargada no hay id que borrar; si se llamara, quedaria eliminada
        coEvery { mixtapeRepository.eliminarMixtape(any()) } returns Result.success(Unit)
        //Act
        viewModel.eliminarMixtape()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.eliminada).isFalse()
    }
}
