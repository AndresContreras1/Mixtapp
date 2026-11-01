package com.example.mixtapp.e2e

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import com.example.mixtapp.MainActivity
import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.conectarEmulador
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.limpiarBase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

// Caso de uso 2: un usuario ya registrado inicia sesion, va al perfil de otro usuario, lo sigue y
// despues encuentra su resena en Siguiendo. Al empezar no sigue a nadie
@HiltAndroidTest
class SeguirUsuarioE2E {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var usuarioRepository: UsuarioRepository

    private val correo = "registrado${System.currentTimeMillis()}@example.com"

    @Before
    fun setUp() {
        hiltRule.inject()
        // Con la aplicacion de prueba de Hilt no corre BaseApplication: el emulador se conecta aqui
        conectarEmulador()

        // El usuario se registra como en la app y cierra la sesion: la prueba entra por el login.
        // usuario_1 (Nombre 1, 10 seguidores) escribio review_1 del album_1
        runBlocking {
            limpiarBase(Firebase.firestore)
            cargarDatosDePrueba(Firebase.firestore)
            authRepository.signUp(correo, "123456")
            usuarioRepository.registrarUsuario(username = "registrado", nombre = "Usuario Registrado", bio = "")
            authRepository.signOut()
        }
    }

    @After
    fun tearDown() = runTest {
        // Se borra la cuenta de la prueba y lo que quedo en la base
        Firebase.auth.currentUser?.delete()?.await()
        Firebase.auth.signOut()
        limpiarBase(Firebase.firestore)
    }

    private fun esperarTag(tag: String) {
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    @Test
    fun login_seguirAlAutorDeUnaResena_suResenaApareceEnSiguiendo() {
        // Inicia sesion
        composeRule.onNodeWithTag("txtEmail").performTextInput(correo)
        composeRule.onNodeWithTag("txtPassword").performTextInput("123456")
        composeRule.onNodeWithTag("btnLogin").performClick()
        esperarTag("homeScreen")

        // Va al perfil de un usuario: el autor de la primera resena de la primera publicacion
        esperarTag("tarjetaTendencia")
        composeRule.onNodeWithTag("tarjetaTendencia").performClick()
        esperarTag("tituloItem")
        composeRule.onNodeWithTag("listaDetalle").performScrollToNode(hasTestTag("autor_review_1"))
        composeRule.onNodeWithTag("autor_review_1").performClick()

        // La informacion del usuario es la correcta
        esperarTag("nombrePerfil")
        composeRule.onNodeWithTag("nombrePerfil").assertTextEquals("Nombre 1")
        composeRule.onNodeWithTag("contadorSeguidores").assertTextContains("10", substring = true)

        // Lo sigue: el contador de seguidores sube a 11
        composeRule.onNodeWithTag("btnSeguir").performClick()
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodes(hasTestTag("contadorSeguidores") and hasText("11", substring = true))
                .fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("contadorSeguidores").assertTextContains("11", substring = true)

        // Vuelve al inicio (perfil -> detalle -> inicio) y abre Siguiendo: ahi esta su resena
        Espresso.pressBack()
        Espresso.pressBack()
        esperarTag("homeScreen")
        composeRule.onNodeWithTag("btnSiguiendo").performClick()
        esperarTag("review_review_1")
        composeRule.onNodeWithTag("review_review_1").assertIsDisplayed()
    }
}
