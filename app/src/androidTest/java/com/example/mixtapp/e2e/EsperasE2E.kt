package com.example.mixtapp.e2e

import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.platform.app.InstrumentationRegistry
import com.example.mixtapp.R
import com.example.mixtapp.limpiarBase
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

// Lo que comparten los dos casos de uso. Estan aqui para no copiarlos en cada clase

private const val ESPERA_MAXIMA = 10000L

// Lo que llega de Firestore tarda: se espera a que aparezca el nodo con ese testTag
fun ComposeTestRule.esperarTag(tag: String) {
    waitUntil(timeoutMillis = ESPERA_MAXIMA) {
        onAllNodesWithTag(tag).fetchSemanticsNodes().isNotEmpty()
    }
}

// Los contadores cambian por el listener: se espera a que el nodo muestre ese texto. Con
// substring basta con que lo contenga ("11" en "11 seguidores")
fun ComposeTestRule.esperarTexto(tag: String, texto: String, substring: Boolean = false) {
    waitUntil(timeoutMillis = ESPERA_MAXIMA) {
        onAllNodes(hasTestTag(tag) and hasText(texto, substring = substring)).fetchSemanticsNodes().isNotEmpty()
    }
}

// Vuelve a la pantalla anterior con la flecha de la pantalla, como lo haria el usuario. El boton
// atras del sistema (Espresso.pressBack) necesita que la ventana tenga el foco, y en el emulador a
// veces no lo tiene; los clics de Compose no lo necesitan. Si la flecha esta arriba de una lista
// que ya se bajo, primero se sube hasta ella
fun ComposeTestRule.tocarVolver(listaTag: String? = null) {
    val volver = InstrumentationRegistry.getInstrumentation().targetContext.getString(R.string.back)

    if (listaTag != null) {
        onNodeWithTag(listaTag).performScrollToNode(hasContentDescription(volver))
    }
    onNodeWithContentDescription(volver).performClick()
}

// Al terminar se borra la cuenta que creo la prueba, se cierra la sesion y se limpia la base
suspend fun borrarCuentaYLimpiarBase() {
    Firebase.auth.currentUser?.delete()?.await()
    Firebase.auth.signOut()
    limpiarBase(Firebase.firestore)
}
