package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.CredencialesInvalidasException
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.login.LoginViewModel
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

// El inicio de sesion con los repositorios moqueados
@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        usuarioRepository = mockk()
        viewModel = LoginViewModel(authRepository, usuarioRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_credencialesValidas_navega() = runTest {
        //Arrange
        viewModel.updateEmail("ana@example.com")
        viewModel.updateContrasena("123456")
        coEvery { authRepository.signIn(any(), any()) } returns Result.success(Unit)
        coEvery { usuarioRepository.actualizarToken() } returns Result.success(Unit)
        //Act
        viewModel.loginButtonPressed()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.navigate).isTrue()
        assertThat(state.cargando).isFalse()
    }

    @Test
    fun login_contrasenaIncorrecta_errorCredenciales() = runTest {
        //Arrange
        viewModel.updateEmail("ana@example.com")
        viewModel.updateContrasena("654321")
        coEvery { authRepository.signIn(any(), any()) } returns Result.failure(CredencialesInvalidasException())
        //Act
        viewModel.loginButtonPressed()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.navigate).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_credenciales)
    }
}
