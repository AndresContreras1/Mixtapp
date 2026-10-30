package com.example.mixtapp.debug

import android.content.Context
import com.example.mixtapp.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Decide a que Firestore se sube: el del emulador (instancia por defecto) o el real (segunda app)
object SeedRunner {

    private const val NOMBRE_APP_REAL = "real"

    suspend fun alEmulador(): String {
        check(BuildConfig.USE_FIREBASE_EMULATOR) { "Este build no usa el emulador" }
        val resumen = FirebaseSeeder(FirebaseFirestore.getInstance()).cargar()
        return "Emulador: ${resumen.describir()}"
    }

    // La instancia por defecto ya apunta al emulador y no se puede devolver al real. Por eso se crea
    // una segunda FirebaseApp con las mismas opciones, que nunca llama a useEmulator
    suspend fun aFirebaseReal(context: Context, email: String, password: String): String {
        val app = appReal(context)

        // Si las reglas de Firestore piden sesion, se entra con una cuenta real
        if (email.isNotBlank()) {
            FirebaseAuth.getInstance(app).signInWithEmailAndPassword(email, password).await()
        }

        val resumen = FirebaseSeeder(FirebaseFirestore.getInstance(app)).cargar()
        return "Firebase REAL (${app.options.projectId}): ${resumen.describir()}"
    }

    private fun appReal(context: Context): FirebaseApp =
        try {
            FirebaseApp.getInstance(NOMBRE_APP_REAL)
        } catch (e: IllegalStateException) {
            FirebaseApp.initializeApp(context, FirebaseApp.getInstance().options, NOMBRE_APP_REAL)
        }
}
