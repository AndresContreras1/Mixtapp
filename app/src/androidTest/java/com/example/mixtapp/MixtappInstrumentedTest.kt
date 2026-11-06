package com.example.mixtapp

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

// La prueba de la plantilla: confirma que corre dentro de la app y que lee sus recursos
@RunWith(AndroidJUnit4::class)
class MixtappInstrumentedTest {

    @Test
    fun targetContext_paquete_esMixtapp() {
        //Arrange
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        //Act
        val paquete = contexto.packageName
        //Assert
        assertThat(paquete).isEqualTo("com.example.mixtapp")
    }

    @Test
    fun targetContext_nombreDeLaApp_esMixtapp() {
        //Arrange
        val contexto = InstrumentationRegistry.getInstrumentation().targetContext
        //Act
        val nombre = contexto.getString(R.string.app_name)
        //Assert
        assertThat(nombre).isEqualTo("Mixtapp")
    }
}
