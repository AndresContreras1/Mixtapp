package com.example.mixtapp.data.datasource.impl.android

import android.media.AudioAttributes
import android.media.MediaPlayer
import com.example.mixtapp.data.datasource.ReproductorDataSource
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.volumenDelFade
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

// Cada cuanto se ajusta el volumen del fade
private const val PASO_DEL_FADE_MS = 100L

// El audio con el MediaPlayer de Android: descarga el mp3 mientras suena, sin guardar nada. Como la
// ubicacion, es algo del celular y va en impl/android
class ReproductorAndroidDataSourceImpl @Inject constructor() : ReproductorDataSource {

    // El MediaPlayer responde con callbacks: callbackFlow los convierte en el flujo de estados y
    // awaitClose lo libera cuando se deja de escuchar, como los listeners de Firestore
    override fun reproducir(url: String): Flow<EstadoDeReproduccion> = callbackFlow {
        trySend(EstadoDeReproduccion.Cargando)

        val reproductor = MediaPlayer()
        reproductor.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
        )
        // Empieza en silencio y el fade sube el volumen
        reproductor.setOnPreparedListener {
            it.setVolume(0f, 0f)
            it.start()
            trySend(EstadoDeReproduccion.Sonando)
        }
        reproductor.setOnCompletionListener {
            trySend(EstadoDeReproduccion.Terminada)
            close()
        }
        reproductor.setOnErrorListener { _, que, extra ->
            close(IOException("MediaPlayer: $que, $extra"))
            true
        }
        reproductor.setDataSource(url)
        reproductor.prepareAsync()

        // El fade de entrada y de salida: el volumen sigue a la posicion del audio
        val fade = launch {
            while (isActive) {
                try {
                    if (reproductor.isPlaying) {
                        val volumen = volumenDelFade(
                            posicionMs = reproductor.currentPosition,
                            duracionMs = reproductor.duration,
                        )
                        reproductor.setVolume(volumen, volumen)
                    }
                } catch (e: IllegalStateException) {
                    // El reproductor ya se libero: no hay nada que ajustar
                }
                delay(PASO_DEL_FADE_MS)
            }
        }

        awaitClose {
            fade.cancel()
            reproductor.release()
        }
    }
}
