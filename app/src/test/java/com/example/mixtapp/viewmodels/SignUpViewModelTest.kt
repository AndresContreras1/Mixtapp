package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.CorreoYaRegistradoException
import com.example.mixtapp.data.repository.DatosDeRegistroInvalidosException
import com.example.mixtapp.data.repository.ErrorInesperadoException
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.signup.SignUpViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
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

// El registro con los repositorios moqueados: el ViewModel recibe el dispatcher de prueba y
// advanceUntilIdle espera a que terminen sus corrutinas antes de los asserts
@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var viewModel: SignUpViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        usuarioRepository = mockk()
        viewModel = SignUpViewModel(authRepository, usuarioRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // El formulario completo: sin un campo nunca se llega al registro
    private fun llenarFormulario(
        contrasena: String = "123456",
        confirmar: String = contrasena,
        usuario: String = "ana",
        correo: String = "ana@example.com",
    ) {
        viewModel.updateNombre("Ana Torres")
        viewModel.updateUsuario(usuario)
        viewModel.updateEmail(correo)
        viewModel.updateContrasena(contrasena)
        viewModel.updateConfirmarContrasena(confirmar)
        viewModel.alternarTerminos()
    }

    @Test
    fun signUp_datosValidos_navega() = runTest {
        //Arrange
        llenarFormulario()
        coEvery { authRepository.signUp(any(), any()) } returns Result.success(Unit)
        coEvery { usuarioRepository.registrarUsuario(any(), any(), any()) } returns Result.success(Unit)
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.navigate).isTrue()
        assertThat(state.errorMessageRes).isNull()
    }

    @Test
    fun signUp_contrasenaDe4_errorContrasenaCorta() = runTest {
        //Arrange: sin mocks, la validacion para el flujo antes de llamar al repositorio
        llenarFormulario(contrasena = "1234")
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.navigate).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_contrasena_corta)
    }

    @Test
    fun signUp_correoYaRegistrado_errorCorreoYaRegistrado() = runTest {
        //Arrange: si signUp falla no se llega a registrarUsuario, asi que no se moquea
        llenarFormulario()
        coEvery { authRepository.signUp(any(), any()) } returns Result.failure(CorreoYaRegistradoException())
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.navigate).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_correo_ya_registrado)
    }

    @Test
    fun signUp_fallaGuardarLosDatos_noNavegaYPideIniciarSesion() = runTest {
        //Arrange: la cuenta se crea en Authentication pero Firestore falla; la sesion se cierra
        llenarFormulario()
        coEvery { authRepository.signUp(any(), any()) } returns Result.success(Unit)
        coEvery { usuarioRepository.registrarUsuario(any(), any(), any()) } returns Result.failure(ErrorInesperadoException())
        every { authRepository.signOut() } returns Unit
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.navigate).isFalse()
        assertThat(state.cargando).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.error_registro_datos)
    }

    @Test
    fun signUp_correoConEspacios_seRegistraSinEspacios() = runTest {
        //Arrange: el mock solo responde al correo sin los espacios del teclado
        llenarFormulario(correo = "  ana@example.com ")
        coEvery { authRepository.signUp("ana@example.com", "123456") } returns Result.success(Unit)
        coEvery { usuarioRepository.registrarUsuario("ana", "Ana Torres", "") } returns Result.success(Unit)
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.navigate).isTrue()
    }

    @Test
    fun signUp_usuarioConEspacios_errorUsuarioConEspacios() = runTest {
        //Arrange: sin mocks, la validacion para el flujo antes de llamar al repositorio
        llenarFormulario(usuario = "ana torres")
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorMessageRes).isEqualTo(R.string.error_username_espacios)
    }

    @Test
    fun signUp_correoSinArroba_errorCorreoInvalido() = runTest {
        //Arrange
        llenarFormulario(correo = "ana.example.com")
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorMessageRes).isEqualTo(R.string.error_email_invalido)
    }

    @Test
    fun signUp_contrasenasDistintas_errorNoCoinciden() = runTest {
        //Arrange
        llenarFormulario(contrasena = "123456", confirmar = "654321")
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorMessageRes).isEqualTo(R.string.error_contrasenas_no_coinciden)
    }

    @Test
    fun signUp_authenticationRechazaLosDatos_errorDatosInvalidos() = runTest {
        //Arrange
        llenarFormulario()
        coEvery { authRepository.signUp(any(), any()) } returns Result.failure(DatosDeRegistroInvalidosException())
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorMessageRes).isEqualTo(R.string.error_datos_registro_invalidos)
    }

    @Test
    fun signUp_sinAceptarTerminos_errorTerminos() = runTest {
        //Arrange: el formulario completo, pero los terminos quedan sin marcar
        llenarFormulario()
        viewModel.alternarTerminos()
        //Act
        viewModel.signUpButtonPressed()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorMessageRes).isEqualTo(R.string.error_terminos)
    }
}
