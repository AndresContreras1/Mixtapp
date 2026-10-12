package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.example.mixtapp.data.model.SearchCategoryUi
import javax.inject.Inject

// Los albumes y sus resenas vienen del backend; aqui solo quedan las categorias de la busqueda
class AlbumLocalDataSource @Inject constructor() {

    suspend fun getSearchCategories(): List<SearchCategoryUi> =
        LocalSearchCategoriesProvider.categories
}
