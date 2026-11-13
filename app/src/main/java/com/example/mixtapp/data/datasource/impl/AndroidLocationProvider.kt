package com.example.mixtapp.data.datasource.impl

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.mixtapp.data.datasource.LocationProvider
import com.example.mixtapp.data.model.Coordenadas
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

// La ubicacion con el LocationManager de Android: no usa servicios de Google
class AndroidLocationProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : LocationProvider {

    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Con permiso de ubicacion precisa se usa el GPS; con la aproximada, la red. Sin permiso no se
    // pide nada y la resena se guarda sin ubicacion
    override suspend fun getCurrentLocation(): Coordenadas? {
        val precisa = tienePermiso(Manifest.permission.ACCESS_FINE_LOCATION)
        val aproximada = tienePermiso(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (!precisa && !aproximada) return null

        val proveedores = if (precisa) {
            listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        } else {
            listOf(LocationManager.NETWORK_PROVIDER)
        }
        val proveedor = proveedores.firstOrNull { locationManager.isProviderEnabled(it) } ?: return null

        return try {
            // Desde Android 11 se pide la ubicacion actual; antes, la ultima que conocio el celular
            val ubicacion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ubicacionActual(proveedor) ?: locationManager.getLastKnownLocation(proveedor)
            } else {
                locationManager.getLastKnownLocation(proveedor)
            }

            ubicacion?.let { Coordenadas(latitud = it.latitude, longitud = it.longitude) }
        } catch (e: SecurityException) {
            // El permiso se quito mientras la app estaba abierta
            Log.d("AndroidLocationProvider", "getCurrentLocation: ${e.message}")
            null
        }
    }

    private fun tienePermiso(permiso: String): Boolean =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED

    // getCurrentLocation responde con un callback: suspendCancellableCoroutine lo convierte en una
    // funcion suspend que espera la respuesta
    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun ubicacionActual(proveedor: String): Location? =
        suspendCancellableCoroutine { continuacion ->
            try {
                locationManager.getCurrentLocation(proveedor, null, context.mainExecutor) { ubicacion ->
                    continuacion.resume(ubicacion)
                }
            } catch (e: SecurityException) {
                continuacion.resume(null)
            }
        }
}
