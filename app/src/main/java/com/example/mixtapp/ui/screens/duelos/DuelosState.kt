package com.example.mixtapp.ui.screens.duelos

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.DueloEnVivoUi
import com.example.mixtapp.data.model.DueloUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.puedeVotar
import com.example.mixtapp.ui.screens.duelos.model.dueloTabs
import com.example.mixtapp.ui.utils.OpcionUi

data class DuelosState(
    val tabs: List<OpcionUi> = dueloTabs,
    val tipoElegido: String = TIPO_ALBUM,
    // El catalogo (para armar el par de hoy) se lee una vez; los votos de cada duelo llegan en vivo
    val items: List<ItemUi> = emptyList(),
    val enVivo: Map<String, DueloEnVivoUi> = emptyMap(),
    // El duelo de hoy de cada tipo, ya armado; falta el tipo que no tiene dos items del mismo genero
    val duelos: Map<String, DueloUi> = emptyMap(),
    // El formulario del voto: por cual voy a votar y mi opinion. Se limpia al votar o al cambiar de
    // pestana
    val opcionElegida: String? = null,
    val opinion: String = "",
    val enviando: Boolean = false,
    // Se pone en true al pedir el catalogo y en false cuando llega o falla
    val isLoading: Boolean = false,
    // El error del catalogo tapa la pantalla. Los de los votos van por tipo, para que el flujo de
    // una pestana no borre el error de otra, y el del voto va aparte
    @StringRes val errorMessageRes: Int? = null,
    val erroresDeVotos: Map<String, Int> = emptyMap(),
    @StringRes val errorVotoRes: Int? = null,
) {
    // El boton Votar queda apagado hasta elegir uno de los dos; la regla esta en data/model
    val puedeVotar: Boolean get() = puedeVotar(opcionElegida = opcionElegida, miOpcion = duelos[tipoElegido]?.miOpcion)
}
