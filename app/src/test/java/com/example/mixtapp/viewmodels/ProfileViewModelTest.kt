package com.example.mixtapp.viewmodels

import android.net.Uri
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.MixtapeUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.UsuarioUi
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.CuotaExcedidaException
import com.example.mixtapp.data.repository.MixtapeRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.StorageRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.profile.ProfileViewModel
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

// Mi perfil con los repositorios moqueados: mis datos, mis listas (cada una con su carga y su
// error, para que una que falla no tape a las demas), la cuenta sin documento y la foto
@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var storageRepository: StorageRepository
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var mixtapeRepository: MixtapeRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        storageRepository = mockk()
        reviewRepository = mockk()
        usuarioRepository = mockk()
        mixtapeRepository = mockk()
        every { authRepository.currentUser?.uid } returns "yo"
        every { authRepository.currentUser?.photoUrl } returns null
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // El ViewModel escucha todo en su init: los flujos se moquean antes de crearlo
    private fun crearViewModel(
        perfil: Flow<UsuarioUi> = flowOf(LocalPreviewProvider.usuario),
        resenas: Flow<List<ReviewUi>> = flowOf(listOf(LocalPreviewProvider.miReview)),
        guardados: Flow<List<ItemResumenUi>> = flowOf(LocalPreviewProvider.favoritos),
        mixtapes: Flow<List<MixtapeUi>> = flowOf(listOf(LocalPreviewProvider.mixtape)),
    ): ProfileViewModel {
        every { usuarioRepository.getMiPerfilLive() } returns perfil
        every { reviewRepository.getMyReviewsLive() } returns resenas
        every { usuarioRepository.getMisFavoritosLive() } returns flowOf(LocalPreviewProvider.favoritos)
        every { usuarioRepository.getMisGuardadosLive() } returns guardados
        every { mixtapeRepository.getMisMixtapesLive(any()) } returns mixtapes
        return ProfileViewModel(authRepository, storageRepository, reviewRepository, usuarioRepository, mixtapeRepository, testDispatcher)
    }

    @Test
    fun init_todoLlega_sinCargasYConMisDatos() = runTest {
        //Arrange
        val viewModel = crearViewModel()
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.cargandoResenas).isFalse()
        assertThat(state.cargandoGuardados).isFalse()
        assertThat(state.cargandoMixtapes).isFalse()
        assertThat(state.usuario).isEqualTo(LocalPreviewProvider.usuario.username)
        assertThat(state.resenasRecientes).hasSize(1)
        assertThat(state.mixtapes).hasSize(1)
    }

    @Test
    fun init_misGuardadosFallan_soloEsaListaTieneError() = runTest {
        //Arrange
        val viewModel = crearViewModel(guardados = flow { throw SinConexionException() })
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.cargandoGuardados).isFalse()
        assertThat(state.errorGuardadosRes).isEqualTo(R.string.error_sin_conexion)
        assertThat(state.errorResenasRes).isNull()
        assertThat(state.errorMixtapesRes).isNull()
        assertThat(state.errorMessageRes).isNull()
    }

    @Test
    fun init_cuentaSinDocumento_usuarioDelCorreoYAvisoDeCompletarlo() = runTest {
        //Arrange: no es un error, es una cuenta que todavia no completo su perfil
        every { authRepository.usuarioDelCorreo } returns "ana"
        val viewModel = crearViewModel(perfil = flow { throw ContenidoNoEncontradoException() })
        //Act
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.usuario).isEqualTo("ana")
        assertThat(state.avisoPerfilRes).isEqualTo(R.string.completa_tu_perfil)
        assertThat(state.errorMessageRes).isNull()
    }

    @Test
    fun subirFotoDePerfil_cuotaExcedida_errorDeLaFotoSinSubiendo() = runTest {
        //Arrange
        val uri = mockk<Uri>()
        val viewModel = crearViewModel()
        coEvery { storageRepository.uploadProfileImage(uri = uri) } returns Result.failure(CuotaExcedidaException())
        advanceUntilIdle()
        //Act
        viewModel.subirFotoDePerfil(uri = uri)
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.subiendoImagen).isFalse()
        assertThat(state.errorImagenRes).isEqualTo(R.string.error_cuota_excedida)
    }

    @Test
    fun subirFotoDePerfil_funciona_seVeLaFotoNueva() = runTest {
        //Arrange
        val uri = mockk<Uri>()
        val viewModel = crearViewModel()
        coEvery { storageRepository.uploadProfileImage(uri = uri) } returns Result.success("https://foto")
        advanceUntilIdle()
        //Act
        viewModel.subirFotoDePerfil(uri = uri)
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.profileImageUrl).isEqualTo("https://foto")
    }
}
