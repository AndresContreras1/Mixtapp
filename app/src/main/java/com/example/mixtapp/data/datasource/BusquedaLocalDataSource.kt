package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.data.model.SearchCategoryUi
import javax.inject.Inject

// Los albumes, las canciones y los artistas vienen de la base remota; aqui solo quedan las tarjetas
// de Buscar. Es suspend como las funciones remotas: si estos datos pasan a la base remota, el
// ViewModel no cambia
class BusquedaLocalDataSource @Inject constructor() {

    suspend fun getSearchCategories(): List<SearchCategoryUi> =
        LocalSearchCategoriesProvider.categories
}
