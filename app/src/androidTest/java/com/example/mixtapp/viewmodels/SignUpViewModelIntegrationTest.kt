package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.MessagingRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.limpiarBase
import com.example.mixtapp.ui.screens.signup.SignUpViewModel
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// El ViewModel del registro con los repositorios reales, contra el emulador de Firebase. El
// ViewModel lanza sus corrutinas y la prueba no avanza a los asserts hasta que el estado cambia
class SignUpViewModelIntegrationTest {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private lateinit var viewModel: SignUpViewModel
    private val correo = "integracion${System.currentTimeMillis()}@example.com"

    @Before
    fun setUp() = runTest {
        try {
            db.useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) {

        }
        try {
            auth.useEmulator("10.0.2.2", 9099)
        } catch (e: Exception) {

        }

        limpiarBase(db)
        auth.signOut()

        val authDataSource = AuthRemoteDataSource(auth)
        viewModel = SignUpViewModel(
            AuthRepository(authDataSource),
            UsuarioRepository(
                UsuarioFirestoreDataSourceImpl(db),
                authDataSource,
                MessagingRemoteDataSource(FirebaseMessaging.getInstance()),
            ),
        )
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
        // La cuenta de la prueba se borra aunque la prueba haya cerrado la sesion
        val user = auth.currentUser ?: runCatching {
            auth.signInWithEmailAndPassword(correo, "123456").await().user
        }.getOrNull()
        user?.delete()?.await()
        auth.signOut()
    }

    private fun llenarFormulario() {
        viewModel.updateNombre("Ana Torres")
        viewModel.updateUsuario("ana")
        viewModel.updateEmail(correo)
        viewModel.updateContrasena("123456")
        viewModel.updateConfirmarContrasena("123456")
        viewModel.alternarTerminos()
    }

    @Test
    fun signUp_datosValidos_navegaYGuardaElUsuario() = runTest {
        //Arrange
        llenarFormulario()
        //Act
        viewModel.signUpButtonPressed()
        //Assert: espera hasta que navigate sea verdadero
        val navigate = viewModel.uiState.map { it.navigate }.first { it }
        Truth.assertThat(navigate).isTrue()
        val usuario = UsuarioFirestoreDataSourceImpl(db).getUsuarioById(auth.currentUser?.uid ?: "")
        Truth.assertThat(usuario?.toUsuarioUi()?.nombre).isEqualTo("Ana Torres")
        Truth.assertThat(usuario?.toUsuarioUi()?.username).isEqualTo("ana")
    }

    @Test
    fun signUp_correoYaRegistrado_muestraElError() = runTest {
        //Arrange: el correo ya tiene una cuenta en el emulador
        auth.createUserWithEmailAndPassword(correo, "123456").await()
        auth.signOut()
        llenarFormulario()
        //Act
        viewModel.signUpButtonPressed()
        //Assert: navigate nunca cambia, asi que se espera a que llegue el error
        val error = viewModel.uiState.map { it.errorMessageRes }.first { it != null }
        Truth.assertThat(error).isEqualTo(R.string.error_correo_ya_registrado)
        Truth.assertThat(viewModel.uiState.value.navigate).isFalse()
    }
}
