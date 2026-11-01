package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.ui.screens.detail.DetailViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
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

// El detalle con los repositorios moqueados: el flujo en vivo se reemplaza por uno que emite una
// vez (o falla) y se revisa el estado que queda
@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catalogoRepository = mockk()
        reviewRepository = mockk()
        viewModel = DetailViewModel(catalogoRepository, reviewRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getItem_itemQueNoExiste_mensajeNoEncontrado() = runTest {
        //Arrange
        coEvery { catalogoRepository.getItemLive(TIPO_ALBUM, "album_999") } returns
            flow<ItemConResenasUi> { throw ContenidoNoEncontradoException() }
        //Act
        viewModel.getItem(TIPO_ALBUM, "album_999")
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.itemConResenas).isNull()
        assertThat(state.errorMessageRes).isEqualTo(R.string.album_no_encontrado)
    }

    @Test
    fun sendOrDeleteLikeItem_sinConexion_errorDeLaAccion() = runTest {
        //Arrange: el item ya esta en pantalla
        coEvery { catalogoRepository.getItemLive(TIPO_ALBUM, "album_from_zero") } returns
            flowOf(LocalPreviewProvider.itemConResenas)
        coEvery { catalogoRepository.sendOrDeleteLike(any()) } returns Result.failure(SinConexionException())
        viewModel.getItem(TIPO_ALBUM, "album_from_zero")
        advanceUntilIdle()
        //Act
        viewModel.sendOrDeleteLikeItem()
        advanceUntilIdle()
        //Assert: el item se sigue viendo y el error es del boton
        val state = viewModel.uiState.value
        assertThat(state.itemConResenas).isNotNull()
        assertThat(state.errorMessageRes).isNull()
        assertThat(state.errorAccionRes).isEqualTo(R.string.error_sin_conexion)
    }
}
