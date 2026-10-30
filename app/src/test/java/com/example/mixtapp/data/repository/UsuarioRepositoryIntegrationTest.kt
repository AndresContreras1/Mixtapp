package com.example.mixtapp.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.testing.borrarDocumento
import com.example.mixtapp.testing.cerrarSesionDePrueba
import com.example.mixtapp.testing.crearUsuario
import com.example.mixtapp.testing.db
import com.example.mixtapp.testing.iniciarSesionDePrueba
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UsuarioRepositoryIntegrationTest {

    private val otroId = "test_otro_usuario_repo"
    private lateinit var miUid: String
    private lateinit var repository: UsuarioRepository

    @Before
    fun preparar() = runBlocking<Unit> {
        miUid = iniciarSesionDePrueba("repo_usuario@mixtapp.test")
        repository = UsuarioRepository(
            UsuarioFirestoreDataSourceImpl(db()),
            AuthRemoteDataSource(FirebaseAuth.getInstance())
        )

        crearUsuario(miUid, nombre = "Laura Test")
        crearUsuario(otroId, nombre = "Otro Usuario")
    }

    @After
    fun limpiar() = runBlocking<Unit> {
        borrarDocumento("usuarios", miUid)
        borrarDocumento("usuarios", otroId)
        cerrarSesionDePrueba()
    }

    @Test
    fun getMiPerfil_devuelveMiPerfil() = runBlocking<Unit> {
        val resultado = repository.getMiPerfil()

        assertTrue(resultado.isSuccess)
        val perfil = resultado.getOrThrow()
        assertEquals(miUid, perfil.id)
        assertEquals("Laura Test", perfil.nombre)
    }

    @Test
    fun getMiPerfil_sinDocumento_devuelveContenidoNoEncontrado() = runBlocking<Unit> {
        borrarDocumento("usuarios", miUid)

        val resultado = repository.getMiPerfil()

        assertTrue(resultado.exceptionOrNull() is ContenidoNoEncontradoException)
    }
}
