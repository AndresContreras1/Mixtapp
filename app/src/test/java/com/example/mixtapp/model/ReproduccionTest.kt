package com.example.mixtapp.model

import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.busquedaDeVistaPrevia
import com.example.mixtapp.data.model.volumenDelFade
import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Las reglas del fragmento de 30 segundos: que se busca en Deezer y el volumen del fade
class ReproduccionTest {

    @Test
    fun busquedaDeVistaPrevia_cancion_artistaYTitulo() {
        //Arrange
        val cancion = LocalPreviewProvider.emptinessMachine
        //Act
        val busqueda = busquedaDeVistaPrevia(cancion)
        //Assert
        assertThat(busqueda).isEqualTo("${cancion.artista} ${cancion.titulo}")
    }

    @Test
    fun busquedaDeVistaPrevia_artista_soloSuNombre() {
        //Arrange
        val artista = LocalPreviewProvider.mikeShinoda
        //Act
        val busqueda = busquedaDeVistaPrevia(artista)
        //Assert
        assertThat(busqueda).isEqualTo(artista.titulo)
    }

    @Test
    fun volumenDelFade_alEmpezar_enSilencio() {
        //Act
        val volumen = volumenDelFade(posicionMs = 0, duracionMs = 30_000)
        //Assert
        assertThat(volumen).isEqualTo(0f)
    }

    @Test
    fun volumenDelFade_enLaMitad_volumenCompleto() {
        //Act
        val volumen = volumenDelFade(posicionMs = 15_000, duracionMs = 30_000)
        //Assert
        assertThat(volumen).isEqualTo(1f)
    }

    @Test
    fun volumenDelFade_unSegundoAntesDelFinal_aLaMitad() {
        //Act
        val volumen = volumenDelFade(posicionMs = 29_000, duracionMs = 30_000)
        //Assert
        assertThat(volumen).isEqualTo(0.5f)
    }
}
