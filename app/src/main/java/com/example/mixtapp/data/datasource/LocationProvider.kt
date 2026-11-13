package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.model.Coordenadas

// La ubicacion del celular, para guardar donde se publica una resena y centrar el mapa
interface LocationProvider {
    // null si no hay permiso de ubicacion o si el celular no la pudo leer
    suspend fun getCurrentLocation(): Coordenadas?
}
