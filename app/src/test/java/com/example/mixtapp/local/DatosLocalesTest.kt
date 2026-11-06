package com.example.mixtapp.local

import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.local.LocalSearchCategoriesProvider
import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Los datos que quedan fijos en la app: las tarjetas de Buscar y los ejemplos de los previews
class DatosLocalesTest {

    @Test
    fun categories_ids_noSeRepiten() {
        //Arrange
        val categorias = LocalSearchCategoriesProvider.categories
        //Act
        val ids = categorias.map { it.id }
        //Assert
        assertThat(ids).containsNoDuplicates()
    }

    @Test
    fun categories_soloGeneros_sonLosDelCatalogo() {
        //Arrange
        val categorias = LocalSearchCategoriesProvider.categories
        //Act
        val generos = categorias.filter { it.esGenero }.map { it.id }
        //Assert
        assertThat(generos).containsExactly("Rock", "Metal", "Nu metal", "Reggaetón", "Música popular").inOrder()
    }

    @Test
    fun categories_imagenes_sonUrls() {
        //Arrange
        val categorias = LocalSearchCategoriesProvider.categories
        //Act
        val sinUrl = categorias.filter { !it.imagenUrl.startsWith("https://") }
        //Assert
        assertThat(sinUrl).isEmpty()
    }

    @Test
    fun reviews_calificaciones_vanDeCeroACinco() {
        //Arrange
        val reviews = LocalPreviewProvider.reviews
        //Act
        val fueraDeRango = reviews.filter { it.rating !in 0..5 }
        //Assert
        assertThat(fueraDeRango).isEmpty()
    }

    @Test
    fun catalogo_promedios_vanDeCeroACinco() {
        //Arrange
        val catalogo = LocalPreviewProvider.catalogo
        //Act
        val fueraDeRango = catalogo.filter { it.rating !in 0.0..5.0 }
        //Assert
        assertThat(fueraDeRango).isEmpty()
    }
}
