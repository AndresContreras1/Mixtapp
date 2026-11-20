package com.example.mixtapp.ui.utils

import android.media.MediaPlayer
import android.net.Uri
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RawRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import java.util.concurrent.atomic.AtomicBoolean

// Un video corto de fondo que se repite sin sonido, recortado para llenar la pantalla, con un velo
// oscuro encima para que el contenido se lea. El video viene dentro de la app (res/raw): no se
// descarga ni pasa por Firebase. Es decoracion de la pantalla, como una imagen, por eso el
// MediaPlayer vive aqui y no en la capa de datos. En el preview, donde no hay video, queda el fondo
// normal de la cuenta
@Composable
fun FondoDeVideo(
    @RawRes video: Int,
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) {
        AppBackground(tono = TonoDeFondo.Cuenta, modifier = modifier)
        return
    }

    val context = LocalContext.current
    val uri = remember { Uri.parse("android.resource://${context.packageName}/$video") }
    // Despues de liberarlo ya no se puede tocar: la superficie puede destruirse despues
    val liberado = remember { AtomicBoolean(false) }
    val reproductor = remember {
        MediaPlayer().apply {
            // Llena la pantalla recortando los bordes, sin franjas negras
            setOnPreparedListener { listo ->
                listo.setVideoScalingMode(MediaPlayer.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING)
                listo.start()
            }
        }
    }

    // Al salir de la pantalla el reproductor se libera
    DisposableEffect(Unit) {
        onDispose {
            liberado.set(true)
            reproductor.release()
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { contexto ->
                SurfaceView(contexto).apply {
                    holder.addCallback(object : SurfaceHolder.Callback {
                        override fun surfaceCreated(holder: SurfaceHolder) {
                            if (liberado.get()) return
                            reproductor.setDataSource(context, uri)
                            reproductor.isLooping = true
                            reproductor.setVolume(0f, 0f)
                            reproductor.setDisplay(holder)
                            reproductor.prepareAsync()
                        }

                        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) = Unit

                        override fun surfaceDestroyed(holder: SurfaceHolder) {
                            // La superficie se destruye cuando la app va al fondo: el reproductor
                            // vuelve a empezar y al volver se prepara otra vez
                            if (!liberado.get()) reproductor.reset()
                        }
                    })
                }
            },
        )

        // El velo: oscuro abajo, donde van los campos, y mas suave arriba, donde va el logo
        val fondo = MaterialTheme.colorScheme.background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(fondo.copy(alpha = 0.45f), fondo.copy(alpha = 0.7f), fondo.copy(alpha = 0.92f))
                    )
                )
        )
    }
}
