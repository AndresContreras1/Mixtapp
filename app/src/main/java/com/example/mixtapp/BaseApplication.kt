package com.example.mixtapp

import android.app.Application
import android.os.Build
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import dagger.hilt.android.HiltAndroidApp

// Punto de entrada de Hilt: aqui se crean los objetos del modulo al arrancar la app. Tambien arma el
// cargador de imagenes de Coil que usan todos los AsyncImage, con los decodificadores de GIF y WebP
// animados para las animaciones de los artistas
@HiltAndroidApp
class BaseApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)

        // En modo debug la app usa el emulador de Firebase (Firebase CLI) que corre en el computador:
        // desde el emulador de Android el computador es 10.0.2.2. BuildConfig es el de la app, no el
        // de Firebase
        if (BuildConfig.DEBUG) {
            Firebase.firestore.useEmulator("10.0.2.2", 8080)
            Log.d("BaseApplication", "Emulando Firestore")
            Firebase.auth.useEmulator("10.0.2.2", 9099)
        }
    }

    // Desde Android 9 ImageDecoder anima GIF y WebP; antes solo hay decodificador de GIF
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
    }
}
