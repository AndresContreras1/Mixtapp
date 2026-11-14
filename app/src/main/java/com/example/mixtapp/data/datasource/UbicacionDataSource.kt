package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.UbicacionDto

// La ubicacion del celular, para guardar donde se publica una resena y centrar el mapa
interface UbicacionDataSource {
    // null si no hay permiso de ubicacion o si el celular no la pudo leer. Si quitan el permiso
    // mientras se pide, lanza la excepcion y el repositorio decide que hacer
    suspend fun getUbicacionActual(): UbicacionDto?
}
