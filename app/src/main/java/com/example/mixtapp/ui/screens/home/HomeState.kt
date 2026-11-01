package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.home.model.FILTRO_PARA_TI
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi
import com.example.mixtapp.ui.screens.home.model.homeFilters

data class HomeState(
    val filters: List<HomeFilterUi> = homeFilters,
    val selectedFilterId: String = FILTRO_PARA_TI,
    // Lo que llega en vivo: con esto el ViewModel arma lo que pinta la pantalla
    val catalogo: List<ItemConResenasUi> = emptyList(),
    val favoritos: List<ItemResumenUi> = emptyList(),
    val seguidosIds: Set<String> = emptySet(),
    // El album de la tarjeta grande; null si el catalogo no tiene albumes
    val enTendencia: ItemUi? = null,
    // Las tres filas del filtro elegido, hasta 10 items cada una
    val canciones: List<ItemUi> = emptyList(),
    val albumes: List<ItemUi> = emptyList(),
    val artistas: List<ItemUi> = emptyList(),
    // Las resenas de quienes sigo, la mas nueva primero
    val resenasDeSeguidos: List<ReviewUi> = emptyList(),
    // Arranca cargando porque lo primero que hace el ViewModel es escuchar el catalogo
    val isLoading: Boolean = true,
    // El error del catalogo tapa la pantalla. Los de favoritos y seguidos van aparte para que un
    // flujo no borre el error del otro, y el del like se muestra sobre las resenas
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorFavoritosRes: Int? = null,
    @StringRes val errorSeguidosRes: Int? = null,
    @StringRes val errorAccionRes: Int? = null,
)
