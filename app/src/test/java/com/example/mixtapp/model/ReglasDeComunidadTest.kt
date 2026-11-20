package com.example.mixtapp.model

import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.generoEnComun
import com.example.mixtapp.data.model.idDelChat
import com.example.mixtapp.data.model.porcentajeDeAfinidad
import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Las reglas de la comunidad: el porcentaje de match, el genero en comun y el id del chat
class ReglasDeComunidadTest {

    private fun like(id: String, genero: String) =
        ItemResumenUi(id = id, tipo = TIPO_ALBUM, titulo = id, subtitulo = "", imagenUrl = "", genero = genero)

    @Test
    fun porcentajeDeAfinidad_mismosLikes_cien() {
        //Arrange
        val likes = listOf(like("a", "Metal"), like("b", "Rock"))
        //Act
        val porcentaje = porcentajeDeAfinidad(misLikes = likes, susLikes = likes)
        //Assert
        assertThat(porcentaje).isEqualTo(100)
    }

    @Test
    fun porcentajeDeAfinidad_sinLikes_cero() {
        //Act
        val porcentaje = porcentajeDeAfinidad(misLikes = emptyList(), susLikes = listOf(like("a", "Metal")))
        //Assert
        assertThat(porcentaje).isEqualTo(0)
    }

    @Test
    fun porcentajeDeAfinidad_mismoGeneroSinItemsEnComun_soloLaParteDelGenero() {
        //Arrange: los dos solo tienen metal, pero ningun item en comun
        val mios = listOf(like("a", "Metal"))
        val suyos = listOf(like("b", "Metal"))
        //Act
        val porcentaje = porcentajeDeAfinidad(misLikes = mios, susLikes = suyos)
        //Assert: 0 de items y 1 de generos, que pesa 0.4
        assertThat(porcentaje).isEqualTo(40)
    }

    @Test
    fun generoEnComun_compartenRock_rock() {
        //Arrange
        val mios = listOf(like("a", "Metal"), like("b", "Rock"))
        val suyos = listOf(like("c", "Rock"), like("d", "Reggaetón"))
        //Act
        val genero = generoEnComun(misLikes = mios, susLikes = suyos)
        //Assert
        assertThat(genero).isEqualTo("Rock")
    }

    @Test
    fun idDelChat_enCualquierOrden_elMismo() {
        //Act
        val deAna = idDelChat("ana", "beto")
        val deBeto = idDelChat("beto", "ana")
        //Assert
        assertThat(deAna).isEqualTo(deBeto)
    }
}
