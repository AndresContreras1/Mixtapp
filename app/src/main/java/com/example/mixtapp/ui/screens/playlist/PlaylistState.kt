package com.example.mixtapp.ui.screens.playlist

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.PlaylistUi

data class PlaylistState(
    // Con esto el ViewModel arma la playlist: el catalogo se lee una vez y mis likes llegan en vivo
    val catalogo: List<ItemConResenasUi> = emptyList(),
    val favoritos: List<ItemResumenUi> = emptyList(),
    // La playlist ya armada; null si todavia no llega o si ya no existe
    val playlist: PlaylistUi? = null,
    // El fragmento que suena: el id de la cancion (null si no suena nada), en que va y el error
    val vistaPreviaId: String? = null,
    val vistaPreviaEstado: EstadoDeReproduccion? = null,
    @StringRes val errorVistaPreviaRes: Int? = null,
    // true mientras suena la playlist completa: al terminar una cancion sigue la siguiente
    val reproduciendoTodo: Boolean = false,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el catalogo
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
)
