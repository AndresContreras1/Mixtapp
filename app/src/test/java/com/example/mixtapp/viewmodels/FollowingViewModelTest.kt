package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.SonandoUi
import com.example.mixtapp.data.repository.ErrorInesperadoException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.data.repository.SonandoRepository
import com.example.mixtapp.ui.screens.following.FollowingViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
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

// Siguiendo con los repositorios moqueados: las resenas de quienes sigo, lo que suena con sus 🔥 y
// que el error de cada parte no tape a la otra
@OptIn(ExperimentalCoroutinesApi::class)
class FollowingViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var socialRepository: SocialRepository
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var sonandoRepository: SonandoRepository

    private val following: FollowingUi = LocalPreviewProvider.following
    private val sonando: List<SonandoUi> = LocalPreviewProvider.sonando

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        socialRepository = mockk()
        reviewRepository = mockk()
        sonandoRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // El ViewModel escucha los dos flujos en su init: se moquean antes de crearlo
    private fun crearViewModel(
        flujoSeguidos: Flow<FollowingUi> = flowOf(following),
        flujoSonando: Flow<List<SonandoUi>> = flowOf(sonando),
    ): FollowingViewModel {
        every { socialRepository.getFollowingLive() } returns flujoSeguidos
        every { sonandoRepository.getSonandoLive(any()) } returns flujoSonando
        return FollowingViewModel(socialRepository, reviewRepository, sonandoRepository, testDispatcher)
    }

    @Test
    fun init_seguidosYSonando_llegaTodoSinCarga() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.reviews.map { it.id }).containsExactly("usuario_2_1", "usuario_1_1").inOrder()
        assertThat(state.sonando.map { it.usuarioId }).containsExactly("yo", "laura").inOrder()
    }

    @Test
    fun init_seguidosFallan_errorYSinCarga() = runTest {
        //Arrange
        val viewModel = crearViewModel(flujoSeguidos = flow { throw SinConexionException() })
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_sin_conexion)
    }

    @Test
    fun init_sonandoFalla_lasResenasSiguenYElErrorVaAparte() = runTest {
        //Arrange
        val viewModel = crearViewModel(flujoSonando = flow { throw SinConexionException() })
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.errorSonandoRes).isEqualTo(R.string.error_sin_conexion)
        assertThat(state.errorMessageRes).isNull()
        assertThat(state.reviews).hasSize(2)
    }

    @Test
    fun reaccionar_falla_errorDeLaAccionSinBorrarLaFila() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        coEvery { sonandoRepository.reaccionar(sonando.last()) } returns Result.failure(ErrorInesperadoException())
        advanceUntilIdle()
        //Act
        viewModel.reaccionar(sonando = sonando.last())
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.errorAccionSonandoRes).isEqualTo(R.string.error_reaccionar)
        assertThat(state.errorSonandoRes).isNull()
        assertThat(state.sonando).hasSize(2)
    }

    @Test
    fun reaccionar_funciona_limpiaElErrorAnterior() = runTest {
        //Arrange: primero falla y despues funciona
        val viewModel = crearViewModel()
        coEvery { sonandoRepository.reaccionar(any()) } returns Result.failure(ErrorInesperadoException())
        advanceUntilIdle()
        viewModel.reaccionar(sonando = sonando.last())
        advanceUntilIdle()
        coEvery { sonandoRepository.reaccionar(any()) } returns Result.success(Unit)
        //Act
        viewModel.reaccionar(sonando = sonando.last())
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorAccionSonandoRes).isNull()
    }

    @Test
    fun quitarSonando_falla_errorDeLoQueSuena() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        coEvery { sonandoRepository.quitarSonando() } returns Result.failure(ErrorInesperadoException())
        advanceUntilIdle()
        //Act
        viewModel.quitarSonando()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorAccionSonandoRes).isEqualTo(R.string.error_sonando)
    }

    @Test
    fun sendOrDeleteLike_sinConexion_errorDeLaAccion() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        coEvery { reviewRepository.sendOrDeleteLike("usuario_2_1") } returns Result.failure(SinConexionException())
        advanceUntilIdle()
        //Act
        viewModel.sendOrDeleteLike(reviewId = "usuario_2_1")
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorAccionRes).isEqualTo(R.string.error_sin_conexion)
    }

    @Test
    fun updateFriendQuery_parteDelNombre_soloSusResenas() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        advanceUntilIdle()
        //Act: sin importar las mayusculas ni los espacios
        viewModel.updateFriendQuery(" diego ")
        //Assert
        assertThat(viewModel.uiState.value.reviews.map { it.author }).containsExactly("Diego Alfaro")
    }
}
