package com.example.mixtapp.dtos

import com.example.mixtapp.data.dtos.formatoCantidad
import com.example.mixtapp.data.dtos.haceCuanto
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
}
