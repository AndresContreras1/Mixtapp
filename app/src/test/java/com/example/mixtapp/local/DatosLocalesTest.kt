package com.example.mixtapp

import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

// Los datos que quedan fijos en la app: las tarjetas de Buscar y los ejemplos de los previews
class DatosLocalesTest {

    @Test
    fun categorias_ids_noSeRepiten() {
        val ids = LocalSearchCategoriesProvider.categories.map { it.id }

        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun categorias_generos_sonLosDelCatalogo() {
        val generos = LocalSearchCategoriesProvider.categories.filter { it.esGenero }.map { it.id }

        assertEquals(listOf("Rock", "Metal", "Reggaetón", "Música popular"), generos)
    }

    @Test
    fun categorias_imagenes_sonUrls() {
        LocalSearchCategoriesProvider.categories.forEach { categoria ->
            assertTrue(categoria.imagenUrl.startsWith("https://"))
        }
    }

    @Test
    fun ejemplos_calificaciones_vanDeCeroACinco() {
        LocalPreviewProvider.reviews.forEach { review ->
            assertTrue(review.rating in 0..5)
        }
        LocalPreviewProvider.catalogo.forEach { itemConResenas ->
            assertTrue(itemConResenas.rating in 0.0..5.0)
        }
    }
}
