package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.home.model.FILTRO_PARA_TI
import com.example.mixtapp.ui.screens.home.model.homeFilters
import com.example.mixtapp.ui.utils.OpcionUi

data class HomeState(
    val filters: List<OpcionUi> = homeFilters,
    val selectedFilterId: String = FILTRO_PARA_TI,
    // Lo que llega en vivo: con esto el ViewModel arma lo que pinta la pantalla
    val catalogo: List<ItemConResenasUi> = emptyList(),
    val favoritos: List<ItemResumenUi> = emptyList(),
    val seguidosIds: Set<String> = emptySet(),
    // El album de la tarjeta grande; null si el catalogo no tiene albumes
    val enTendencia: ItemUi? = null,
    // El id del album en tendencia de mis generos que salio al azar al entrar al inicio. Se guarda
    // para que un like o una resena nueva, que vuelven a armar el inicio, no lo cambien
    val albumEnTendenciaId: String? = null,
    // En la pestana Tendencias la tarjeta es el puesto 1 de Colombia y dice su puesto en esa lista
    val enTendenciaColombia: Boolean = false,
    // El fragmento de 30 segundos: el id del item que suena o carga (null si no suena nada), en que va
    // y el error si no se pudo reproducir
    val vistaPreviaId: String? = null,
    val vistaPreviaEstado: EstadoDeReproduccion? = null,
    @StringRes val errorVistaPreviaRes: Int? = null,
    // Las tres filas del filtro elegido, hasta 10 items cada una
    val canciones: List<ItemUi> = emptyList(),
    val albumes: List<ItemUi> = emptyList(),
    val artistas: List<ItemUi> = emptyList(),
    // Las resenas de quienes sigo, la mas nueva primero
    val resenasDeSeguidos: List<ReviewUi> = emptyList(),
    // Se pone en true al empezar a escuchar el catalogo y en false cuando llega o falla
    val isLoading: Boolean = false,
    // El error del catalogo tapa la pantalla. Los de favoritos y seguidos van aparte para que un
    // flujo no borre el error del otro, y el del like se muestra sobre las resenas
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorFavoritosRes: Int? = null,
    @StringRes val errorSeguidosRes: Int? = null,
    @StringRes val errorAccionRes: Int? = null,
)
