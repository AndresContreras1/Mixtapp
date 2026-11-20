package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ComunidadRepository
import com.example.mixtapp.ui.screens.foro.ForoViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
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

// El foro con los repositorios moqueados: el item llega una vez y los mensajes por un flujo
@OptIn(ExperimentalCoroutinesApi::class)
class ForoViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var comunidadRepository: ComunidadRepository
    private lateinit var viewModel: ForoViewModel

    private val item = LocalPreviewProvider.fromZero

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catalogoRepository = mockk()
        comunidadRepository = mockk()
        coEvery { catalogoRepository.getItem(TIPO_ALBUM, item.id) } returns Result.success(item)
        every { comunidadRepository.getMensajesDelForoLive(item.id) } returns flowOf(emptyList())
        viewModel = ForoViewModel(catalogoRepository, comunidadRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun abrirForo_itemQueExiste_loMuestra() = runTest {
        //Act
        viewModel.abrirForo(TIPO_ALBUM, item.id)
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.item?.id).isEqualTo(item.id)
        assertThat(viewModel.uiState.value.isLoading).isFalse()
    }

    @Test
    fun enviar_sinTexto_avisaYNoPublica() = runTest {
        //Arrange
        viewModel.abrirForo(TIPO_ALBUM, item.id)
        advanceUntilIdle()
        //Act
        viewModel.enviar()
        //Assert
        assertThat(viewModel.uiState.value.errorEnviarRes).isEqualTo(R.string.error_mensaje_vacio)
        coVerify(exactly = 0) { comunidadRepository.publicarEnForo(any(), any()) }
    }

    @Test
    fun enviar_conTexto_publicaYLimpiaElCampo() = runTest {
        //Arrange
        coEvery { comunidadRepository.publicarEnForo(item, "Qué discazo") } returns Result.success(Unit)
        viewModel.abrirForo(TIPO_ALBUM, item.id)
        advanceUntilIdle()
        viewModel.updateTexto("  Qué discazo  ")
        //Act
        viewModel.enviar()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.texto).isEmpty()
        assertThat(viewModel.uiState.value.errorEnviarRes).isNull()
    }
}
