package com.example.mixtapp.ui.utils

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

// Los dos permisos de ubicacion: desde Android 12 se piden juntos y el usuario elige si da la
// precisa o la aproximada
private val PERMISOS_DE_UBICACION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION,
)

// Pide el permiso de ubicacion al entrar a la pantalla, como el de notificaciones: solo si todavia
// no esta concedido. Si se concede (o ya lo estaba) avisa con onConcedido; si se niega, la pantalla
// sigue igual sin ubicacion. La usan el mapa y Escribir resena
@Composable
fun PedirPermisoDeUbicacion(onConcedido: () -> Unit = {}) {
    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { resultados ->
            if (resultados.values.any { it }) {
                onConcedido()
            } else {
                Log.d("PermisoDeUbicacion", "Permiso de ubicacion negado")
            }
        }
    )

    LaunchedEffect(Unit) {
        val concedido = PERMISOS_DE_UBICACION.any { permiso ->
            ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED
        }

        if (concedido) onConcedido() else launcher.launch(PERMISOS_DE_UBICACION)
    }
}
