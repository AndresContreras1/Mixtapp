package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.UbicacionDataSource
import com.example.mixtapp.data.dtos.toCoordenadas
import com.example.mixtapp.data.model.Coordenadas
import javax.inject.Inject

// La ubicacion del celular para el mapa: el data source la lee y aqui se decide el error
class UbicacionRepository @Inject constructor(
    private val ubicacionDataSource: UbicacionDataSource
) {

    // Donde estoy. Sin permiso o sin GPS el resultado es un error y el mapa se queda en Bogota
    suspend fun getUbicacionActual(): Result<Coordenadas> {
        return try {
            val ubicacion = ubicacionDataSource.getUbicacionActual()
                ?: return Result.failure(UbicacionNoDisponibleException())

            Result.success(ubicacion.toCoordenadas())
        } catch (e: SecurityException) {
            // El permiso se quito mientras la app estaba abierta
            Log.d("UbicacionRepository", "getUbicacionActual: ${e.message}")
            Result.failure(SinPermisoDeUbicacionException())
        } catch (e: Exception) {
            Log.d("UbicacionRepository", "getUbicacionActual: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }
}
