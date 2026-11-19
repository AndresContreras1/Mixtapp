package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ReproductorRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.SinVistaPreviaException
import com.example.mixtapp.data.repository.SonandoRepository
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
    private lateinit var sonandoRepository: SonandoRepository
    private lateinit var reproductorRepository: ReproductorRepository
    private lateinit var viewModel: DetailViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        catalogoRepository = mockk()
        reviewRepository = mockk()
        sonandoRepository = mockk()
        reproductorRepository = mockk()
        viewModel = DetailViewModel(
            catalogoRepository, reviewRepository, sonandoRepository, reproductorRepository, testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getItem_itemQueExiste_loMuestraSinError() = runTest {
        //Arrange
        coEvery { catalogoRepository.getItemLive(TIPO_ALBUM, "album_from_zero") } returns
            flowOf(LocalPreviewProvider.itemConResenas)
        //Act
        viewModel.getItem(TIPO_ALBUM, "album_from_zero")
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.itemConResenas?.item?.id).isEqualTo(LocalPreviewProvider.itemConResenas.item.id)
        assertThat(state.errorMessageRes).isNull()
    }

    @Test
    fun ponerSonando_itemCargado_avisoDePublicado() = runTest {
        //Arrange: el item ya esta en pantalla
        coEvery { catalogoRepository.getItemLive(TIPO_ALBUM, "album_from_zero") } returns
            flowOf(LocalPreviewProvider.itemConResenas)
        coEvery { sonandoRepository.ponerSonando(any()) } returns Result.success(Unit)
        viewModel.getItem(TIPO_ALBUM, "album_from_zero")
        advanceUntilIdle()
        //Act
        viewModel.ponerSonando()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.avisoSonandoRes).isEqualTo(R.string.sonando_publicado)
        assertThat(state.errorAccionRes).isNull()
    }

    @Test
    fun ponerSonando_sinConexion_errorYSinAviso() = runTest {
        //Arrange
        coEvery { catalogoRepository.getItemLive(TIPO_ALBUM, "album_from_zero") } returns
            flowOf(LocalPreviewProvider.itemConResenas)
        coEvery { sonandoRepository.ponerSonando(any()) } returns Result.failure(SinConexionException())
        viewModel.getItem(TIPO_ALBUM, "album_from_zero")
        advanceUntilIdle()
        //Act
        viewModel.ponerSonando()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.avisoSonandoRes).isNull()
        assertThat(state.errorAccionRes).isEqualTo(R.string.error_sin_conexion)
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
        assertThat(state.errorMessageRes).isEqualTo(R.string.item_no_encontrado)
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

    // Carga el item para que el play tenga que sonar
    private fun conElItem() {
        coEvery { catalogoRepository.getItemLive(TIPO_ALBUM, "album_from_zero") } returns
            flowOf(LocalPreviewProvider.itemConResenas)
        viewModel.getItem(TIPO_ALBUM, "album_from_zero")
    }

    @Test
    fun alternarVistaPreviaDelItem_hayFragmento_quedaSonando() = runTest {
        //Arrange
        conElItem()
        coEvery { reproductorRepository.getVistaPrevia(any()) } returns Result.success("https://preview.mp3")
        coEvery { reproductorRepository.escuchar("https://preview.mp3") } returns
            flowOf(EstadoDeReproduccion.Cargando, EstadoDeReproduccion.Sonando)
        //Act
        advanceUntilIdle()
        viewModel.alternarVistaPreviaDelItem()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.vistaPreviaId).isEqualTo(LocalPreviewProvider.itemConResenas.item.id)
        assertThat(state.vistaPreviaEstado).isEqualTo(EstadoDeReproduccion.Sonando)
        assertThat(state.errorVistaPreviaRes).isNull()
    }

    @Test
    fun alternarVistaPreviaDelItem_sinFragmento_avisaYNoSuena() = runTest {
        //Arrange
        conElItem()
        coEvery { reproductorRepository.getVistaPrevia(any()) } returns Result.failure(SinVistaPreviaException())
        //Act
        advanceUntilIdle()
        viewModel.alternarVistaPreviaDelItem()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.vistaPreviaId).isNull()
        assertThat(state.vistaPreviaEstado).isNull()
        assertThat(state.errorVistaPreviaRes).isEqualTo(R.string.error_sin_vista_previa)
    }

    @Test
    fun alternarVistaPreviaDelItem_terminaLosTreintaSegundos_vuelveAlPlay() = runTest {
        //Arrange
        conElItem()
        coEvery { reproductorRepository.getVistaPrevia(any()) } returns Result.success("https://preview.mp3")
        coEvery { reproductorRepository.escuchar("https://preview.mp3") } returns
            flowOf(EstadoDeReproduccion.Sonando, EstadoDeReproduccion.Terminada)
        //Act
        advanceUntilIdle()
        viewModel.alternarVistaPreviaDelItem()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.vistaPreviaId).isNull()
        assertThat(viewModel.uiState.value.vistaPreviaEstado).isNull()
    }

    @Test
    fun detenerVistaPrevia_mientrasSuena_quedaSinSonar() = runTest {
        //Arrange
        conElItem()
        coEvery { reproductorRepository.getVistaPrevia(any()) } returns Result.success("https://preview.mp3")
        coEvery { reproductorRepository.escuchar("https://preview.mp3") } returns flowOf(EstadoDeReproduccion.Sonando)
        advanceUntilIdle()
        viewModel.alternarVistaPreviaDelItem()
        advanceUntilIdle()
        //Act
        viewModel.detenerVistaPrevia()
        //Assert
        assertThat(viewModel.uiState.value.vistaPreviaId).isNull()
        assertThat(viewModel.uiState.value.vistaPreviaEstado).isNull()
    }
}
