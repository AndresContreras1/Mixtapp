package com.example.mixtapp.dtos

import com.example.mixtapp.data.dtos.ArtistaDto
import com.example.mixtapp.data.dtos.CancionDto
import com.example.mixtapp.data.dtos.busquedaEnYoutube
import com.example.mixtapp.data.dtos.formatoCantidad
import com.example.mixtapp.data.dtos.haceCuanto
import com.example.mixtapp.data.dtos.perfilDeInstagram
import com.example.mixtapp.data.dtos.toItemUi
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant

// El mapeo deja los numeros, las fechas y los links listos para la pantalla
class FormatosTest {

    // Una fecha fija: asi la prueba da lo mismo cualquier dia que se corra
    private val ahora = Instant.parse("2026-10-31T12:00:00Z")

    @Test
    fun formatoCantidad_menosDeMil_quedaIgual() {
        //Act
        val texto = formatoCantidad(950)
        //Assert
        assertThat(texto).isEqualTo("950")
    }

    @Test
    fun formatoCantidad_milesConDecimal_usaKConUnDecimal() {
        //Act
        val texto = formatoCantidad(1_200)
        //Assert
        assertThat(texto).isEqualTo("1.2K")
    }

    @Test
    fun formatoCantidad_masDeDiezMil_usaKSinDecimal() {
        //Act
        val texto = formatoCantidad(14_929)
        //Assert
        assertThat(texto).isEqualTo("14K")
    }

    @Test
    fun formatoCantidad_millones_usaM() {
        //Act
        val texto = formatoCantidad(2_500_000)
        //Assert
        assertThat(texto).isEqualTo("2.5M")
    }

    @Test
    fun haceCuanto_menosDeUnMinuto_quedaVacio() {
        //Act
        val texto = haceCuanto(fechaIso = "2026-10-31T11:59:30Z", ahora = ahora)
        //Assert
        assertThat(texto).isEmpty()
    }

    @Test
    fun haceCuanto_horas_unidadCorta() {
        //Act
        val texto = haceCuanto(fechaIso = "2026-10-31T09:00:00Z", ahora = ahora)
        //Assert
        assertThat(texto).isEqualTo("3 h")
    }

    @Test
    fun haceCuanto_dias_unidadCorta() {
        //Act
        val texto = haceCuanto(fechaIso = "2026-10-29T12:00:00Z", ahora = ahora)
        //Assert
        assertThat(texto).isEqualTo("2 d")
    }

    @Test
    fun haceCuanto_masDeUnaSemana_muestraLaFecha() {
        //Act
        val texto = haceCuanto(fechaIso = "2026-09-01T12:00:00Z", ahora = ahora)
        //Assert
        assertThat(texto).isEqualTo("2026-09-01")
    }

    @Test
    fun busquedaEnYoutube_espaciosYTildes_vanCodificados() {
        //Act
        val url = busquedaEnYoutube("Soda Stereo De Música Ligera")
        //Assert
        assertThat(url).isEqualTo("https://www.youtube.com/results?search_query=Soda%20Stereo%20De%20M%C3%BAsica%20Ligera")
    }

    @Test
    fun toItemUi_cancion_armaLosLinksConArtistaYTitulo() {
        //Arrange
        val dto = CancionDto().copy(titulo = "Numb", artista = "Linkin Park")
        //Act
        val cancion = dto.toItemUi()
        //Assert
        assertThat(cancion.youtubeUrl).isEqualTo("https://www.youtube.com/results?search_query=Linkin%20Park%20Numb%20video%20oficial")
        assertThat(cancion.spotifyUrl).isEqualTo("https://open.spotify.com/search/Linkin%20Park%20Numb")
        assertThat(cancion.artista).isEqualTo("Linkin Park")
    }

    @Test
    fun toItemUi_artistaIntegrante_suBandaYSuInstagram() {
        //Arrange
        val dto = ArtistaDto().copy(nombre = "James Hetfield", grupo = "Metallica", instagram = "metallica")
        //Act
        val artista = dto.toItemUi()
        //Assert
        assertThat(artista.artista).isEqualTo("Metallica")
        assertThat(artista.instagramUrl).isEqualTo("https://www.instagram.com/metallica/")
        assertThat(artista.youtubeUrl).isEmpty()
    }

    @Test
    fun perfilDeInstagram_nulo_quedaVacio() {
        //Act
        val url = perfilDeInstagram(null)
        //Assert
        assertThat(url).isEmpty()
    }

    @Test
    fun perfilDeInstagram_textoVacio_quedaVacio() {
        //Act
        val url = perfilDeInstagram("")
        //Assert
        assertThat(url).isEmpty()
    }
}
