package com.example.mixtapp.data.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mixtapp.data.datasource.impl.firestore.ReviewFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.CreateReviewAlbumDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.testing.borrarDocumento
import com.example.mixtapp.testing.borrarResenasDe
import com.example.mixtapp.testing.crearAlbum
import com.example.mixtapp.testing.crearResena
import com.example.mixtapp.testing.crearUsuario
import com.example.mixtapp.testing.db
import com.example.mixtapp.testing.leer
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReviewFirestoreDataSourceTest {

    private val usuarioId = "test_usuario_review_ds"
    private val albumId = "test_album_review_ds"
    private val resenaId = "test_resena_review_ds"
    private lateinit var datasource: ReviewFirestoreDataSourceImpl

    @Before
    fun preparar() = runBlocking<Unit> {
        datasource = ReviewFirestoreDataSourceImpl(db())
        crearAlbum(albumId)
        crearUsuario(usuarioId)
    }

    @After
    fun limpiar() = runBlocking<Unit> {
        borrarResenasDe(usuarioId)
        borrarDocumento("albumes", albumId)
        borrarDocumento("usuarios", usuarioId)
    }

    private fun dto(calificacion: Int, comentario: String) = CreateReviewDto(
        usuarioId = usuarioId,
        albumId = albumId,
        calificacion = calificacion,
        comentario = comentario,
        fechaEscucha = "2026-10-01",
        usuario = CreateReviewUsuarioDto(nombre = "Usuario de prueba", fotoUrl = null),
        album = CreateReviewAlbumDto(titulo = "Album de prueba", artista = "Artista de prueba", portadaUrl = "")
    )

    @Test
    fun createReview_guardaLaResenaEnFirestore() = runBlocking<Unit> {
        datasource.createReview(dto(calificacion = 4, comentario = "Muy bueno"))

        val docs = db().collection("reviews").whereEqualTo("usuarioId", usuarioId).get().await().documents

        assertEquals(1, docs.size)
        assertEquals(4L, docs[0].getLong("calificacion"))
        assertEquals("Muy bueno", docs[0].getString("comentario"))
    }

    @Test
    fun updateReview_cambiaLosCampos() = runBlocking<Unit> {
        crearResena(resenaId, usuarioId, albumId, calificacion = 5)

        datasource.updateReview(resenaId, dto(calificacion = 2, comentario = "Cambie de opinion"))

        val doc = leer("reviews", resenaId)
        assertEquals(2L, doc.getLong("calificacion"))
        assertEquals("Cambie de opinion", doc.getString("comentario"))
    }

    @Test
    fun deleteReview_borraLaResena() = runBlocking<Unit> {
        crearResena(resenaId, usuarioId, albumId)

        datasource.deleteReview(resenaId)

        assertFalse(leer("reviews", resenaId).exists())
    }
}
