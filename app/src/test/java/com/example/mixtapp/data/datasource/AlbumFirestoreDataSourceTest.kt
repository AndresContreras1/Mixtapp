package com.example.mixtapp.data.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mixtapp.data.datasource.impl.firestore.AlbumFirestoreDataSourceImpl
import com.example.mixtapp.testing.borrarDocumento
import com.example.mixtapp.testing.crearAlbum
import com.example.mixtapp.testing.crearResena
import com.example.mixtapp.testing.crearUsuario
import com.example.mixtapp.testing.db
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlbumFirestoreDataSourceTest {

    private val albumId = "test_album_ds"
    private val usuarioId = "test_usuario_album_ds"
    private val resenaId = "test_resena_album_ds"
    private lateinit var datasource: AlbumFirestoreDataSourceImpl

    @Before
    fun preparar() = runBlocking<Unit> {
        datasource = AlbumFirestoreDataSourceImpl(db())
        crearAlbum(albumId, titulo = "Album de prueba")
        crearUsuario(usuarioId)
        crearResena(resenaId, usuarioId, albumId, calificacion = 5)
    }

    @After
    fun limpiar() = runBlocking<Unit> {
        borrarDocumento("reviews", resenaId, "likes")
        borrarDocumento("albumes", albumId)
        borrarDocumento("usuarios", usuarioId)
    }

    @Test
    fun getAlbumes_incluyeElAlbumGuardado() = runBlocking<Unit> {
        val albumes = datasource.getAlbumes()

        assertTrue(albumes.any { it.id == albumId })
    }

    @Test
    fun getAlbumById_devuelveElAlbumCorrecto() = runBlocking<Unit> {
        val album = datasource.getAlbumById(albumId)

        assertEquals(albumId, album.id)
        assertEquals("Album de prueba", album.titulo)
        assertEquals(2024, album.anio)
    }

    @Test
    fun getAlbumById_inexistente_lanzaNoSuchElementException() = runBlocking<Unit> {
        val resultado = runCatching { datasource.getAlbumById("test_album_que_no_existe") }

        assertTrue(resultado.exceptionOrNull() is NoSuchElementException)
    }

    @Test
    fun getReviewsDeAlbum_devuelveLasResenas() = runBlocking<Unit> {
        val resenas = datasource.getReviewsDeAlbum(albumId)

        assertEquals(1, resenas.size)
        assertEquals(resenaId, resenas[0].id)
    }
}
