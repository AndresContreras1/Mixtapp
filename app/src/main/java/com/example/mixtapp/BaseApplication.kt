package com.example.mixtapp

import android.app.Application
import android.os.Build
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp

// Punto de entrada de Hilt: aqui se crean los objetos del modulo al arrancar la app
@HiltAndroidApp
class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Solo en debug. Se hace aqui y no en el modulo de Hilt porque Application.onCreate corre
        // siempre primero (app, pruebas y pantalla de seed) y antes de que algo use Firebase
        if (BuildConfig.USE_FIREBASE_EMULATOR) conectarAlEmulador()
    }

    private fun conectarAlEmulador() {
        val host = hostDelEmulador()
        try {
            FirebaseAuth.getInstance().useEmulator(host, 9099)
            FirebaseFirestore.getInstance().useEmulator(host, 8080)
            Log.d("BaseApplication", "Conectado a los emuladores de Firebase en $host")
        } catch (e: IllegalStateException) {
            Log.d("BaseApplication", "Emulador ya configurado: ${e.message}")
        }
    }

    private fun hostDelEmulador(): String {
        val esAvd = Build.PRODUCT.contains("sdk") || Build.FINGERPRINT.contains("generic")
        return if (esAvd) "10.0.2.2" else "127.0.0.1"
    }
}
