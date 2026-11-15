package com.example.mixtapp.data.datasource.impl.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.mixtapp.data.datasource.UbicacionDataSource
import com.example.mixtapp.data.dtos.UbicacionDto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

// La ubicacion con el LocationManager de Android: no usa servicios de Google. El LocationManager
// lo entrega Hilt (UbicacionModule), como Firestore o Retrofit
class UbicacionAndroidDataSourceImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val locationManager: LocationManager,
) : UbicacionDataSource {

    // Con permiso de ubicacion precisa se usa el GPS; con la aproximada, la red. Sin permiso o sin
    // proveedor encendido no se pide nada y se retorna null
    override suspend fun getUbicacionActual(): UbicacionDto? {
        val precisa = tienePermiso(Manifest.permission.ACCESS_FINE_LOCATION)
        val aproximada = tienePermiso(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (!precisa && !aproximada) return null

        val proveedores = if (precisa) {
            listOf(LocationManager.GPS_PROVIDER, LocationManager.NETWORK_PROVIDER)
        } else {
            listOf(LocationManager.NETWORK_PROVIDER)
        }
        val proveedor = proveedores.firstOrNull { locationManager.isProviderEnabled(it) } ?: return null

        // Desde Android 11 se pide la ubicacion actual; antes, la ultima que conocio el celular
        val ubicacion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ubicacionActual(proveedor) ?: locationManager.getLastKnownLocation(proveedor)
        } else {
            locationManager.getLastKnownLocation(proveedor)
        }

        return ubicacion?.let { UbicacionDto(latitud = it.latitude, longitud = it.longitude) }
    }

    private fun tienePermiso(permiso: String): Boolean =
        ContextCompat.checkSelfPermission(context, permiso) == PackageManager.PERMISSION_GRANTED

    // getCurrentLocation responde con un callback: suspendCancellableCoroutine lo convierte en una
    // funcion suspend que espera la respuesta
    @RequiresApi(Build.VERSION_CODES.R)
    private suspend fun ubicacionActual(proveedor: String): Location? =
        suspendCancellableCoroutine { continuacion ->
            locationManager.getCurrentLocation(proveedor, null, context.mainExecutor) { ubicacion ->
                continuacion.resume(ubicacion)
            }
        }
}
