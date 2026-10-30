package com.example.mixtapp.data.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.testing.borrarDocumento
import com.example.mixtapp.testing.crearUsuario
import com.example.mixtapp.testing.db
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsuarioFirestoreDataSourceTest {

    private val a = "test_usuario_a_ds"
    private val b = "test_usuario_b_ds"
    private lateinit var datasource: UsuarioFirestoreDataSourceImpl

    @Before
    fun preparar() = runBlocking<Unit> {
        datasource = UsuarioFirestoreDataSourceImpl(db())
        crearUsuario(a, nombre = "Usuario A")
        crearUsuario(b, nombre = "Usuario B")
    }

    @After
    fun limpiar() = runBlocking<Unit> {
        borrarDocumento("usuarios", a)
        borrarDocumento("usuarios", b)
    }

    @Test
    fun getUsuarioById_devuelveElPerfil() = runBlocking<Unit> {
        val usuario = datasource.getUsuarioById(a)

        assertEquals(a, usuario.id)
        assertEquals("Usuario A", usuario.nombre)
    }

    @Test
    fun registrarUsuario_guardaOActualizaPerfil() = runBlocking<Unit> {
        datasource.registrarUsuario(
            RegistrarUsuarioDto(nombre = "Nombre nuevo", username = "nuevo_user", bio = null, fotoUrl = null),
            a
        )

        val usuario = datasource.getUsuarioById(a)
        assertEquals("Nombre nuevo", usuario.nombre)
        assertEquals("nuevo_user", usuario.username)
    }
}
