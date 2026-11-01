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
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.Espresso
import com.example.mixtapp.MainActivity
import com.example.mixtapp.R
import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.conectarEmulador
import com.example.mixtapp.data.repository.AuthRepository
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

// Caso de uso 1: un usuario nuevo se registra (primero con una contrasena de 4 caracteres, que no
// pasa), entra a la primera publicacion, le da like a la primera resena y despues se lo quita
@HiltAndroidTest
class RegistroYLikeE2E {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Inject
    lateinit var authRepository: AuthRepository

    private val correo = "nuevo${System.currentTimeMillis()}@example.com"

    @Before
    fun setUp() {
        hiltRule.inject()
        // Con la aplicacion de prueba de Hilt no corre BaseApplication: el emulador se conecta aqui
        conectarEmulador()

        // La primera publicacion es el album_1 con la resena review_1, sin likes
        runBlocking {
            limpiarBase(Firebase.firestore)
            cargarDatosDePrueba(Firebase.firestore)
            authRepository.signOut()
        }
    }

    @After
    fun tearDown() = runTest {
        // Se borra la cuenta que creo la prueba y lo que quedo en la base
        Firebase.auth.currentUser?.delete()?.await()
        Firebase.auth.signOut()
        limpiarBase(Firebase.firestore)
    }

    private fun esperarTag(tag: String) {
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
        }
    }

    // El contador de likes de la resena llega por el listener: se espera a que muestre el numero
    private fun esperarLikes(reviewId: String, likes: String) {
        composeRule.waitUntil(timeoutMillis = 10000) {
            composeRule.onAllNodes(hasTestTag("likes_$reviewId") and hasText(likes))
                .fetchSemanticsNodes().isNotEmpty()
        }
    }

    // Abre la primera publicacion del inicio y baja hasta la primera resena
    private fun abrirPrimeraPublicacion() {
        esperarTag("tarjetaTendencia")
        composeRule.onNodeWithTag("tarjetaTendencia").performClick()
        esperarTag("tituloItem")
        composeRule.onNodeWithTag("listaDetalle").performScrollToNode(hasTestTag("like_review_1"))
    }

    @Test
    fun registro_contrasenaCortaYLuegoValida_daYQuitaLikeEnLaPrimeraPublicacion() {
        // Registro con contrasena de 4 caracteres: sale el error y no entra
        composeRule.onNodeWithTag("btnIrARegistro").performClick()
        composeRule.onNodeWithTag("txtNombre").performTextInput("Nuevo Usuario")
        composeRule.onNodeWithTag("txtUsername").performTextInput("nuevo")
        composeRule.onNodeWithTag("txtEmailRegistro").performTextInput(correo)
        composeRule.onNodeWithTag("txtPasswordRegistro").performTextInput("1234")
        composeRule.onNodeWithTag("txtConfirmPassword").performTextInput("1234")
        composeRule.onNodeWithTag("chkTerminos").performScrollTo().performClick()
        composeRule.onNodeWithTag("btnRegistrar").performScrollTo().performClick()

        val errorContrasena = composeRule.activity.getString(R.string.error_contrasena_corta)
        composeRule.onNodeWithTag("errorMessage").assertIsDisplayed()
        composeRule.onNodeWithTag("errorMessage").assertTextEquals(errorContrasena)

        // Corrige la contrasena: performTextClearance antes, porque performTextInput escribe al inicio
        composeRule.onNodeWithTag("txtPasswordRegistro").performScrollTo().performTextClearance()
        composeRule.onNodeWithTag("txtPasswordRegistro").performTextInput("123456")
        composeRule.onNodeWithTag("txtConfirmPassword").performScrollTo().performTextClearance()
        composeRule.onNodeWithTag("txtConfirmPassword").performTextInput("123456")
        composeRule.onNodeWithTag("btnRegistrar").performScrollTo().performClick()

        esperarTag("homeScreen")
        composeRule.onNodeWithTag("homeScreen").assertIsDisplayed()

        // La primera publicacion: el detalle muestra el album correcto
        abrirPrimeraPublicacion()
        composeRule.onNodeWithTag("tituloItem").assertTextEquals("Album 1")

        // Like a la primera resena: el contador pasa de 0 a 1
        composeRule.onNodeWithTag("likes_review_1").assertTextEquals("0")
        composeRule.onNodeWithTag("like_review_1").performClick()
        esperarLikes(reviewId = "review_1", likes = "1")
        composeRule.onNodeWithTag("likes_review_1").assertTextContains("1")

        // Vuelve atras, entra otra vez a la publicacion y quita el like: el contador baja a 0
        Espresso.pressBack()
        esperarTag("homeScreen")
        abrirPrimeraPublicacion()
        esperarLikes(reviewId = "review_1", likes = "1")
        composeRule.onNodeWithTag("like_review_1").performClick()
        esperarLikes(reviewId = "review_1", likes = "0")
        composeRule.onNodeWithTag("likes_review_1").assertTextEquals("0")
    }
}
