package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ErrorInesperadoException
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.data.repository.SonandoRepository
import com.example.mixtapp.ui.screens.notifications.NotificationsViewModel
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

// Las notificaciones con los repositorios moqueados: Hoy y Antes, las tres pestanas con su propio
// error y marcar una como leida
@OptIn(ExperimentalCoroutinesApi::class)
class NotificationsViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var socialRepository: SocialRepository
    private lateinit var catalogoRepository: CatalogoRepository
    private lateinit var sonandoRepository: SonandoRepository

    // La primera es de hoy y sin leer; las otras dos son de antes y ya estan leidas
    private val notificaciones: List<NotificationUi> = LocalPreviewProvider.notifications

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        socialRepository = mockk()
        catalogoRepository = mockk()
        sonandoRepository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // El ViewModel escucha las tres pestanas en su init: se moquean antes de crearlo
    private fun crearViewModel(
        flujoNotificaciones: Flow<List<NotificationUi>> = flowOf(notificaciones),
        flujoLanzamientos: Flow<List<ItemUi>> = flowOf(listOf(LocalPreviewProvider.karolG)),
    ): NotificationsViewModel {
        every { socialRepository.getNotificacionesLive() } returns flujoNotificaciones
        every { socialRepository.getFollowingLive() } returns flowOf(LocalPreviewProvider.following)
        every { catalogoRepository.getLanzamientosLive() } returns flujoLanzamientos
        every { sonandoRepository.getSonandoLive(any()) } returns flowOf(LocalPreviewProvider.sonando)
        return NotificationsViewModel(socialRepository, catalogoRepository, sonandoRepository, testDispatcher)
    }

    @Test
    fun init_tresNotificaciones_unaHoyYDosAntes() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.hoy.map { it.id }).containsExactly("1")
        assertThat(state.antes.map { it.id }).containsExactly("2", "3").inOrder()
        assertThat(state.discusiones).hasSize(2)
        assertThat(state.lanzamientos).hasSize(1)
    }

    @Test
    fun init_lanzamientosFallan_soloSuPestanaTieneError() = runTest {
        //Arrange
        val viewModel = crearViewModel(flujoLanzamientos = flow { throw SinConexionException() })
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.cargandoLanzamientos).isFalse()
        assertThat(state.errorLanzamientosRes).isEqualTo(R.string.error_sin_conexion)
        assertThat(state.errorMessageRes).isNull()
        assertThat(state.errorDiscusionesRes).isNull()
    }

    @Test
    fun abrirNotificacion_yaLeida_noLaVuelveAMarcar() = runTest {
        //Arrange: si se marcara, el mock fallaria y quedaria el error
        val viewModel = crearViewModel()
        coEvery { socialRepository.marcarNotificacionLeida(any()) } returns Result.failure(ErrorInesperadoException())
        advanceUntilIdle()
        //Act
        viewModel.abrirNotificacion(notificacion = notificaciones[1])
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorAccionRes).isNull()
    }

    @Test
    fun abrirNotificacion_sinLeerYFalla_errorDeLaAccion() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        coEvery { socialRepository.marcarNotificacionLeida("1") } returns Result.failure(ErrorInesperadoException())
        advanceUntilIdle()
        //Act
        viewModel.abrirNotificacion(notificacion = notificaciones[0])
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorAccionRes).isEqualTo(R.string.error_actualizar_lista)
    }

    @Test
    fun selectTab_lanzamientos_quedaElegida() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        //Act
        viewModel.selectTab(id = "lanzamientos")
        //Assert
        assertThat(viewModel.uiState.value.selectedTabId).isEqualTo("lanzamientos")
    }

    @Test
    fun init_sonando_llegaLaFilaDeQuienesSigo() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        //Act
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.sonando).isEqualTo(LocalPreviewProvider.sonando)
        assertThat(viewModel.uiState.value.errorSonandoRes).isNull()
    }
}
