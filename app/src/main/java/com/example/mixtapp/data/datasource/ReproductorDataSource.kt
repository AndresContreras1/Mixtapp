package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.model.EstadoDeReproduccion
import kotlinx.coroutines.flow.Flow

// El reproductor de audio del celular
interface ReproductorDataSource {
    // Suena el enlace mientras se escuche el flujo: primero Cargando, despues Sonando y, si llega al
    // final, Terminada. Dejar de escucharlo detiene el audio. Si el audio no se puede abrir, el flujo
    // termina con la excepcion y el repositorio decide el error
    fun reproducir(url: String): Flow<EstadoDeReproduccion>
}
