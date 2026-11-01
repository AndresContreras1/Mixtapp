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

// El mapeo deja los numeros y las fechas listos para la pantalla
class FormatosTest {

    private val ahora = Instant.parse("2026-10-31T12:00:00Z")

    @Test
    fun formatoCantidad_menosDeMil_quedaIgual() {
        assertThat(formatoCantidad(950)).isEqualTo("950")
    }

    @Test
    fun formatoCantidad_miles_usaK() {
        assertThat(formatoCantidad(1_200)).isEqualTo("1.2K")
        assertThat(formatoCantidad(14_929)).isEqualTo("14K")
    }

    @Test
    fun formatoCantidad_millones_usaM() {
        assertThat(formatoCantidad(2_500_000)).isEqualTo("2.5M")
    }

    @Test
    fun haceCuanto_menosDeUnMinuto_quedaVacio() {
        assertThat(haceCuanto(fechaIso = "2026-10-31T11:59:30Z", ahora = ahora)).isEmpty()
    }

    @Test
    fun haceCuanto_horasYDias_unidadCorta() {
        assertThat(haceCuanto(fechaIso = "2026-10-31T09:00:00Z", ahora = ahora)).isEqualTo("3 h")
        assertThat(haceCuanto(fechaIso = "2026-10-29T12:00:00Z", ahora = ahora)).isEqualTo("2 d")
    }

    @Test
    fun haceCuanto_masDeUnaSemana_muestraLaFecha() {
        assertThat(haceCuanto(fechaIso = "2026-09-01T12:00:00Z", ahora = ahora)).isEqualTo("2026-09-01")
    }

    @Test
    fun busquedaEnYoutube_espaciosYTildes_vanCodificados() {
        assertThat(busquedaEnYoutube("Soda Stereo De Música Ligera"))
            .isEqualTo("https://www.youtube.com/results?search_query=Soda%20Stereo%20De%20M%C3%BAsica%20Ligera")
    }

    @Test
    fun cancion_mapeo_armaLosLinksConArtistaYTitulo() {
        val cancion = CancionDto().copy(titulo = "Numb", artista = "Linkin Park").toItemUi()

        assertThat(cancion.youtubeUrl).isEqualTo("https://www.youtube.com/results?search_query=Linkin%20Park%20Numb%20video%20oficial")
        assertThat(cancion.spotifyUrl).isEqualTo("https://open.spotify.com/search/Linkin%20Park%20Numb")
        assertThat(cancion.artista).isEqualTo("Linkin Park")
    }

    @Test
    fun artista_integrante_suBandaYSuInstagram() {
        val artista = ArtistaDto().copy(nombre = "James Hetfield", grupo = "Metallica", instagram = "metallica").toItemUi()

        assertThat(artista.artista).isEqualTo("Metallica")
        assertThat(artista.instagramUrl).isEqualTo("https://www.instagram.com/metallica/")
        assertThat(artista.youtubeUrl).isEmpty()
    }

    @Test
    fun perfilDeInstagram_sinUsuario_quedaVacio() {
        assertThat(perfilDeInstagram(null)).isEmpty()
        assertThat(perfilDeInstagram("")).isEmpty()
    }
}
