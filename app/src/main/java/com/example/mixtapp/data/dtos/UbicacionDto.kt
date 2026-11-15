package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.Coordenadas

// La ubicacion que entrega el celular, en grados. El data source la retorna asi y el repositorio
// la pasa al modelo que usa el mapa
data class UbicacionDto(
    val latitud: Double,
    val longitud: Double,
)

fun UbicacionDto.toCoordenadas(): Coordenadas {
    return Coordenadas(
        latitud = latitud,
        longitud = longitud,
    )
}
