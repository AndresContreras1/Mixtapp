package com.example.mixtapp.model

import com.example.mixtapp.data.model.MAX_INSTAGRAM_LENGTH
import com.example.mixtapp.data.model.OPCION_A
import com.example.mixtapp.data.model.esCorreoValido
import com.example.mixtapp.data.model.esFechaDeEscuchaValida
import com.example.mixtapp.data.model.esInstagramValido
import com.example.mixtapp.data.model.esUsuarioValido
import com.example.mixtapp.data.model.hayPocosDatos
import com.example.mixtapp.data.model.puedeVotar
import com.example.mixtapp.data.model.usuarioDelCorreo
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

// Las reglas de data/model son funciones puras: se prueban sin mocks ni corrutinas
class ReglasTest {

    // Un dia fijo: asi la prueba da lo mismo cualquier dia que se corra
    private val hoy = LocalDate.parse("2026-11-14")

    @Test
    fun esCorreoValido_sinArroba_false() {
        //Act
        val valido = esCorreoValido("ana.example.com")
        //Assert
        assertThat(valido).isFalse()
    }

    @Test
    fun usuarioDelCorreo_correo_parteAntesDeLaArroba() {
        //Act
        val usuario = usuarioDelCorreo("ana.torres@example.com")
        //Assert
        assertThat(usuario).isEqualTo("ana.torres")
    }

    @Test
    fun esUsuarioValido_conEspacio_false() {
        //Act
        val valido = esUsuarioValido("ana torres")
        //Assert
        assertThat(valido).isFalse()
    }

    @Test
    fun esInstagramValido_vacio_true() {
        //Act: el Instagram es opcional
        val valido = esInstagramValido("")
        //Assert
        assertThat(valido).isTrue()
    }

    @Test
    fun esInstagramValido_conTilde_false() {
        //Act
        val valido = esInstagramValido("andrés_m")
        //Assert
        assertThat(valido).isFalse()
    }

    @Test
    fun esInstagramValido_pasaDelLimite_false() {
        //Act
        val valido = esInstagramValido("a".repeat(MAX_INSTAGRAM_LENGTH + 1))
        //Assert
        assertThat(valido).isFalse()
    }

    @Test
    fun esFechaDeEscuchaValida_31DeFebrero_false() {
        //Act
        val valida = esFechaDeEscuchaValida("2026-02-31", hoy = hoy)
        //Assert
        assertThat(valida).isFalse()
    }

    @Test
    fun esFechaDeEscuchaValida_manana_false() {
        //Act
        val valida = esFechaDeEscuchaValida("2026-11-15", hoy = hoy)
        //Assert
        assertThat(valida).isFalse()
    }

    @Test
    fun esFechaDeEscuchaValida_hoy_true() {
        //Act
        val valida = esFechaDeEscuchaValida("2026-11-14", hoy = hoy)
        //Assert
        assertThat(valida).isTrue()
    }

    @Test
    fun puedeVotar_sinElegirNiHaberVotado_false() {
        //Act
        val puede = puedeVotar(opcionElegida = null, miOpcion = null)
        //Assert
        assertThat(puede).isFalse()
    }

    @Test
    fun puedeVotar_yaVote_trueParaCambiarLaOpinion() {
        //Act
        val puede = puedeVotar(opcionElegida = null, miOpcion = OPCION_A)
        //Assert
        assertThat(puede).isTrue()
    }

    @Test
    fun hayPocosDatos_unaResenaYUnMeGusta_true() {
        //Act
        val pocos = hayPocosDatos(resenas = 1, meGusta = 1)
        //Assert
        assertThat(pocos).isTrue()
    }

    @Test
    fun hayPocosDatos_tresResenas_false() {
        //Act
        val pocos = hayPocosDatos(resenas = 3, meGusta = 0)
        //Assert
        assertThat(pocos).isFalse()
    }
}
