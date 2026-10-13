package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.data.model.SearchCategoryUi
import javax.inject.Inject

// Los albumes y sus resenas vienen de la base remota; aqui solo quedan las categorias de la busqueda.
// Es suspend como las funciones remotas: si estos datos pasan a la base remota, el ViewModel no cambia
class AlbumLocalDataSource @Inject constructor() {

    suspend fun getSearchCategories(): List<SearchCategoryUi> =
        LocalSearchCategoriesProvider.categories
}
