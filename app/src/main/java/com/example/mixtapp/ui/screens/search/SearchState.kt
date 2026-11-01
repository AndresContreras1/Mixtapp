package com.example.mixtapp.ui.screens.search

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.UsuarioUi

data class SearchState(
    val query: String = "",
    // Las tarjetas de Explorar por (formas de ordenar) y las de Generos
    val ordenes: List<SearchCategoryUi> = emptyList(),
    val generos: List<SearchCategoryUi> = emptyList(),
    // null mientras no se toque una tarjeta
    val categoriaElegida: SearchCategoryUi? = null,
    // Todo el catalogo en vivo: buscar y elegir una categoria trabajan sobre esta lista
    val catalogo: List<ItemConResenasUi> = emptyList(),
    // Ya vienen filtrados y ordenados por el ViewModel
    val resultados: List<ItemConResenasUi> = emptyList(),
    // Las personas que coinciden con el texto, desde 2 letras
    val usuarios: List<UsuarioUi> = emptyList(),
    // Arranca cargando porque lo primero que hace el ViewModel es escuchar el catalogo
    val isLoading: Boolean = true,
    // El error del catalogo tapa la pantalla; el de las categorias y el de las personas van aparte
    @StringRes val errorMessageRes: Int? = null,
    @StringRes val errorCategoriasRes: Int? = null,
    @StringRes val errorUsuariosRes: Int? = null,
)
