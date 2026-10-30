package com.example.mixtapp.data.repository

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewLocalDataSource
import com.example.mixtapp.data.datasource.impl.firestore.AlbumFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.ReviewFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.testing.borrarDocumento
import com.example.mixtapp.testing.borrarResenasDe
import com.example.mixtapp.testing.cerrarSesionDePrueba
import com.example.mixtapp.testing.crearAlbum
import com.example.mixtapp.testing.crearResena
import com.example.mixtapp.testing.crearUsuario
import com.example.mixtapp.testing.db
import com.example.mixtapp.testing.iniciarSesionDePrueba
import com.example.mixtapp.testing.leer
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewRepositoryIntegrationTest {

    private val albumId = "test_album_review_repo"
    private val resenaId = "test_resena_review_repo"
    private lateinit var miUid: String
    private lateinit var repository: ReviewRepository

    @Before
    fun preparar() = runBlocking<Unit> {
        miUid = iniciarSesionDePrueba("repo_review@mixtapp.test")
        val firestore = db()
        repository = ReviewRepository(
            ReviewLocalDataSource(),
            ReviewFirestoreDataSourceImpl(firestore),
            UsuarioFirestoreDataSourceImpl(firestore),
            AlbumFirestoreDataSourceImpl(firestore),
            AuthRemoteDataSource(FirebaseAuth.getInstance())
        )

        crearAlbum(albumId, titulo = "Album de prueba")
        crearUsuario(miUid, nombre = "Laura Test")
    }

    @After
    fun limpiar() = runBlocking<Unit> {
        borrarResenasDe(miUid)
        borrarDocumento("reviews", resenaId)
        borrarDocumento("albumes", albumId)
        borrarDocumento("usuarios", miUid)
        cerrarSesionDePrueba()
    }

    @Test
    fun guardarResena_sinReviewId_creaLaResenaConLaCopiaDeUsuarioYAlbum() = runBlocking<Unit> {
        val resultado = repository.guardarResena(
            albumId = albumId, rating = 4, texto = "Muy bueno", fecha = "2026-10-30", reviewId = null
        )

        assertTrue(resultado.isSuccess)
        val docs = db().collection("reviews").whereEqualTo("usuarioId", miUid).get().await().documents
        assertEquals(1, docs.size)
        assertEquals(4L, docs[0].getLong("calificacion"))
        assertEquals("Laura Test", docs[0].getString("usuario.nombre"))
        assertEquals("Album de prueba", docs[0].getString("album.titulo"))
    }

    @Test
    fun guardarResena_conReviewId_modificaLaResena() = runBlocking<Unit> {
        crearResena(resenaId, miUid, albumId, calificacion = 5)

        val resultado = repository.guardarResena(
            albumId = albumId, rating = 2, texto = "Ya no me gusta", fecha = "2026-10-30", reviewId = resenaId
        )

        assertTrue(resultado.isSuccess)
        val doc = leer("reviews", resenaId)
        assertEquals(2L, doc.getLong("calificacion"))
        assertEquals("Ya no me gusta", doc.getString("comentario"))
    }

    @Test
    fun eliminarResena_borraLaResena() = runBlocking<Unit> {
        crearResena(resenaId, miUid, albumId)

        val resultado = repository.eliminarResena(resenaId)

        assertTrue(resultado.isSuccess)
        assertFalse(leer("reviews", resenaId).exists())
    }
}
