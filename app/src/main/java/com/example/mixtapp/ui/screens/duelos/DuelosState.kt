package com.example.mixtapp.ui.screens.duelos

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.DueloEnVivoUi
import com.example.mixtapp.data.model.DueloUi
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.ui.screens.duelos.model.DueloTabUi
import com.example.mixtapp.ui.screens.duelos.model.dueloTabs

data class DuelosState(
    val tabs: List<DueloTabUi> = dueloTabs,
    val tipoElegido: String = TIPO_ALBUM,
    // Lo que llega en vivo: el catalogo (para armar el par de hoy) y, por tipo, los votos de su duelo
    val catalogo: List<ItemConResenasUi> = emptyList(),
    val enVivo: Map<String, DueloEnVivoUi> = emptyMap(),
    // El duelo de hoy de cada tipo, ya armado; falta el tipo que no tiene dos items del mismo genero
    val duelos: Map<String, DueloUi> = emptyMap(),
    // El formulario del voto: por cual voy a votar y mi opinion. Se limpia al votar o al cambiar de
    // pestana
    val opcionElegida: String? = null,
    val opinion: String = "",
    val enviando: Boolean = false,
    // Arranca cargando porque lo primero que hace el ViewModel es escuchar el catalogo
    val isLoading: Boolean = true,
    // El error del catalogo tapa la pantalla; el de los votos y el del voto van aparte, para que un
    // flujo no borre el error del otro
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorVotosRes: Int? = null,
    @StringRes val errorVotoRes: Int? = null,
)
