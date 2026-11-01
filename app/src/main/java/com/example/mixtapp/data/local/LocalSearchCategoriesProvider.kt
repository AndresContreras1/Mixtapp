package com.example.mixtapp.data.local

import com.example.mixtapp.R
import com.example.mixtapp.data.model.CATEGORIA_FECHA_LANZAMIENTO
import com.example.mixtapp.data.model.CATEGORIA_MAS_POPULARES
import com.example.mixtapp.data.model.CATEGORIA_MEJOR_CALIFICADOS
import com.example.mixtapp.data.model.SearchCategoryUi

// Las tarjetas de Buscar no cambian: van fijas en la app. Cada una usa la portada de un album del
// catalogo que la representa
object LocalSearchCategoriesProvider {
    val categories = listOf(
        SearchCategoryUi(
            id = CATEGORIA_FECHA_LANZAMIENTO,
            titleRes = R.string.categoria_fecha_lanzamiento,
            imagenUrl = "https://cdn-images.dzcdn.net/images/cover/d98eaccfbb945bdf68241d6de7fe6a49/1000x1000-000000-80-0-0.jpg",
            esGenero = false,
        ),
        SearchCategoryUi(
            id = CATEGORIA_MAS_POPULARES,
            titleRes = R.string.categoria_mas_populares,
            imagenUrl = "https://cdn-images.dzcdn.net/images/cover/f794dd1931dac42fc13f938850b686c6/1000x1000-000000-80-0-0.jpg",
            esGenero = false,
        ),
        SearchCategoryUi(
            id = CATEGORIA_MEJOR_CALIFICADOS,
            titleRes = R.string.categoria_mejor_calificados,
            imagenUrl = "https://cdn-images.dzcdn.net/images/cover/f31aab52640d3ef83b6fdee9b4acfe1d/1000x1000-000000-80-0-0.jpg",
            esGenero = false,
        ),
        SearchCategoryUi(
            id = "Rock",
            titleRes = R.string.genero_rock,
            imagenUrl = "https://cdn-images.dzcdn.net/images/cover/f0282817b697279e56df13909962a54a/1000x1000-000000-80-0-0.jpg",
            esGenero = true,
        ),
        SearchCategoryUi(
            id = "Metal",
            titleRes = R.string.genero_metal,
            imagenUrl = "https://cdn-images.dzcdn.net/images/cover/291e2af9295ca885b154eee75dfa0432/1000x1000-000000-80-0-0.jpg",
            esGenero = true,
        ),
        SearchCategoryUi(
            id = "Nu metal",
            titleRes = R.string.genero_nu_metal,
            imagenUrl = "https://cdn-images.dzcdn.net/images/cover/033a271b5ec10842c287827c39244fb5/1000x1000-000000-80-0-0.jpg",
            esGenero = true,
        ),
        SearchCategoryUi(
            id = "Reggaetón",
            titleRes = R.string.genero_reggaeton,
            imagenUrl = "https://cdn-images.dzcdn.net/images/artist/5b0aab23f8d2856951a92f3a9faf70e3/1000x1000-000000-80-0-0.jpg",
            esGenero = true,
        ),
        SearchCategoryUi(
            id = "Música popular",
            titleRes = R.string.genero_musica_popular,
            imagenUrl = "https://cdn-images.dzcdn.net/images/cover/bf25843546476d6a01ce92caf4313ee1/1000x1000-000000-80-0-0.jpg",
            esGenero = true,
        ),
    )
}
