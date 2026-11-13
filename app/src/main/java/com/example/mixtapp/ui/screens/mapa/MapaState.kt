package com.example.mixtapp.ui.screens.mapa

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.Coordenadas
import com.example.mixtapp.data.model.ResenaEnMapaUi

// Si no hay permiso de ubicacion, el mapa arranca en Bogota
val CENTRO_POR_DEFECTO = Coordenadas(latitud = 4.6533, longitud = -74.0836)

data class MapaState(
    // Las resenas de las ultimas 24 horas con ubicacion: cada una es un marcador
    val resenas: List<ResenaEnMapaUi> = emptyList(),
    // El id de la resena del marcador que toque; null si no hay ninguno elegido
    val seleccionadaId: String? = null,
    // Donde se centra el mapa: mi ubicacion si dio permiso, o Bogota
    val centro: Coordenadas = CENTRO_POR_DEFECTO,
    // Arranca cargando porque lo primero que hace el ViewModel es escuchar las resenas
    val isLoading: Boolean = true,
    // No se pudieron cargar: sale encima del mapa
    @StringRes val errorMessageRes: Int? = null,
    // Fallo un like en la tarjeta de la resena elegida
    @StringRes val errorAccionRes: Int? = null,
) {
    // La resena elegida sale de la lista, asi su tarjeta se actualiza sola con cada like
    val seleccionada: ResenaEnMapaUi? get() = resenas.find { it.review.id == seleccionadaId }
}
