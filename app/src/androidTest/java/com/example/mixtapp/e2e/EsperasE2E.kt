package com.example.mixtapp.e2e

import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
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

// Al terminar se borra la cuenta que creo la prueba, se cierra la sesion y se limpia la base
suspend fun borrarCuentaYLimpiarBase() {
    Firebase.auth.currentUser?.delete()?.await()
    Firebase.auth.signOut()
    limpiarBase(Firebase.firestore)
}
