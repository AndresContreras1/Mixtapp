package com.example.mixtapp.dtos

import com.example.mixtapp.data.dtos.ItemDelDueloDto
import com.example.mixtapp.data.dtos.UsuarioDto
import com.example.mixtapp.data.dtos.VotoDto
import com.example.mixtapp.data.dtos.toItemResumenUi
import com.example.mixtapp.data.dtos.toVotoUi
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.DueloUi
import com.example.mixtapp.data.model.OPCION_A
import com.example.mixtapp.data.model.OPCION_B
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.data.model.toItemResumenUi
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.Instant

// Lo que el duelo calcula para la pantalla (porcentajes y ganador) y el mapeo de sus documentos
class DueloMapeosTest {

    private val ahora = Instant.parse("2026-11-05T12:00:00Z")

    private fun duelo(votosA: Int, votosB: Int) = DueloUi(
        id = "dia_1_album",
        dia = 1,
        tipo = "album",
        genero = "Nu metal",
        itemA = LocalPreviewProvider.fromZero.toItemResumenUi(),
        itemB = LocalPreviewProvider.karolG.toItemResumenUi(),
        votosA = votosA,
        votosB = votosB,
        opiniones = emptyList(),
        miOpcion = null,
    )

    @Test
    fun porcentajeA_sinVotos_mitadYMitad() {
        //Act
        val duelo = duelo(votosA = 0, votosB = 0)
        //Assert
        assertThat(duelo.porcentajeA).isEqualTo(50)
        assertThat(duelo.porcentajeB).isEqualTo(50)
    }

    @Test
    fun porcentajeA_tresContraUno_setentaYCincoYVeinticinco() {
        //Act
        val duelo = duelo(votosA = 3, votosB = 1)
        //Assert
        assertThat(duelo.porcentajeA).isEqualTo(75)
        assertThat(duelo.porcentajeB).isEqualTo(25)
    }

    @Test
    fun ganador_masVotosB_esB() {
        //Act
        val duelo = duelo(votosA = 2, votosB = 5)
        //Assert
        assertThat(duelo.ganador).isEqualTo(OPCION_B)
    }

    @Test
    fun ganador_empate_ninguno() {
        //Act
        val duelo = duelo(votosA = 4, votosB = 4)
        //Assert
        assertThat(duelo.ganador).isNull()
    }

    @Test
    fun toVotoUi_votoDeOtro_mapeaAutorOpinionYHaceCuanto() {
        //Arrange
        val dto = VotoDto(
            id = "laura",
            opcion = OPCION_A,
            opinion = "El riff",
            usuario = UsuarioDto(id = "laura", nombre = "Laura", fotoUrl = null),
            createdAt = "2026-11-05T09:00:00Z",
        )
        //Act
        val voto = dto.toVotoUi(usuarioActualId = "yo", ahora = ahora)
        //Assert
        assertThat(voto.autor).isEqualTo("Laura")
        assertThat(voto.autorFoto).isEmpty()
        assertThat(voto.opinion).isEqualTo("El riff")
        assertThat(voto.timeAgo).isEqualTo("3 h")
        assertThat(voto.esMio).isFalse()
    }

    @Test
    fun toItemResumenUi_itemDelDuelo_llevaElTipoDelDuelo() {
        //Arrange
        val dto = ItemDelDueloDto(id = "cancion_numb", titulo = "Numb", subtitulo = "Meteora", imagenUrl = "")
        //Act
        val item = dto.toItemResumenUi(tipo = TIPO_CANCION)
        //Assert
        assertThat(item.tipo).isEqualTo(TIPO_CANCION)
        assertThat(item.titulo).isEqualTo("Numb")
    }
}
