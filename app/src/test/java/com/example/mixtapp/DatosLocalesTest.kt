package com.example.mixtapp

import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.local.LocalSongReviewProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DatosLocalesTest {

    @Test
    fun cadaResenaTieneUnIdUnico() {
        val ids = LocalMyReviewsProvider.reviews.map { it.id }

        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun cadaAlbumTieneUnIdUnico() {
        val ids = LocalSongReviewProvider.songs.map { it.id }

        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun lasCalificacionesDeLasResenasVanDeCeroACinco() {
        LocalMyReviewsProvider.reviews.forEach { resena ->
            assertTrue(resena.rating in 0..5)
        }
    }

    @Test
    fun lasCalificacionesDeLosAlbumesVanDeCeroACinco() {
        LocalSongReviewProvider.songs.forEach { album ->
            assertTrue(album.rating >= 0.0 && album.rating <= 5.0)

            album.reviews.forEach { resena ->
                assertTrue(resena.rating in 0..5)
            }
        }
    }

    @Test
    fun cadaResenaAbreUnAlbumQueExiste() {
        val idsDeAlbumes = LocalSongReviewProvider.songs.map { it.id }.toSet()

        LocalMyReviewsProvider.reviews.forEach { resena ->
            assertTrue(resena.songId in idsDeAlbumes)
        }
    }
}
