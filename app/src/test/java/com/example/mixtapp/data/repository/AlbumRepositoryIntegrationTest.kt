package com.example.mixtapp.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mixtapp.data.datasource.AlbumLocalDataSource
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.AlbumFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.testing.borrarDocumento
import com.example.mixtapp.testing.cerrarSesionDePrueba
import com.example.mixtapp.testing.crearAlbum
import com.example.mixtapp.testing.crearResena
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
class AlbumRepositoryIntegrationTest {

    private val albumId = "test_album_repo"
    private val albumVacioId = "test_album_vacio_repo"
    private val autores = listOf("test_autor_repo_1", "test_autor_repo_2", "test_autor_repo_3")
    private val resenas = listOf("test_resena_repo_1", "test_resena_repo_2", "test_resena_repo_3")
    private val resenaMiaId = "test_resena_repo_mia"
    private lateinit var miUid: String
    private lateinit var repository: AlbumRepository

    @Before
    fun preparar() = runBlocking<Unit> {
        miUid = iniciarSesionDePrueba("repo_album@mixtapp.test")
        val firestore = db()
        repository = AlbumRepository(
            AlbumLocalDataSource(),
            AlbumFirestoreDataSourceImpl(firestore),
            UsuarioFirestoreDataSourceImpl(firestore),
            AuthRemoteDataSource(FirebaseAuth.getInstance())
        )

        crearAlbum(albumId, titulo = "Album con resenas")
        crearAlbum(albumVacioId, titulo = "Album sin resenas")
        autores.forEach { crearUsuario(it) }
        // Calificaciones controladas: 5, 4 y 2
        crearResena(resenas[0], autores[0], albumId, calificacion = 5)
        crearResena(resenas[1], autores[1], albumId, calificacion = 4)
        crearResena(resenas[2], autores[2], albumId, calificacion = 2)
    }

    @After
    fun limpiar() = runBlocking<Unit> {
        (resenas + resenaMiaId).forEach { borrarDocumento("reviews", it) }
        borrarDocumento("albumes", albumId)
        borrarDocumento("albumes", albumVacioId)
        autores.forEach { borrarDocumento("usuarios", it) }
        borrarDocumento("usuarios", miUid)
        cerrarSesionDePrueba()
    }

    @Test
    fun getSongReviews_calculaPromedios() = runBlocking<Unit> {
        val resultado = repository.getSongReviews()

        assertTrue(resultado.isSuccess)
        val albumes = resultado.getOrThrow()
        val album = albumes.firstOrNull { it.album.id == albumId }
        assertTrue(album != null)
        if (album != null) {
            assertEquals("Album con resenas", album.album.title)
            assertEquals(3, album.reviews.size)
            assertEquals("3", album.ratingCount)
            assertEquals(11.0 / 3, album.rating, 0.001)
            assertEquals(66, album.recommendRate)
        }
    }
}
