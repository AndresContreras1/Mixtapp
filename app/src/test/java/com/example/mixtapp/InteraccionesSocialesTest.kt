package com.example.mixtapp

import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.dtos.toMyReviewUi
import com.example.mixtapp.data.dtos.toSongReviewItemUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InteraccionesSocialesTest {

    @Test
    fun documentosAnterioresSinRelacionesMuestranCero() {
        val review = ReviewConUsuarioDto().toSongReviewItemUi("usuario")
        assertEquals(0, review.likes)
        assertFalse(review.isLiked)
        assertEquals(0, ReviewConAlbumDto().toMyReviewUi().likes)
        assertEquals(0, UsuarioPerfilFirestoreDto().toUsuarioUi().seguidores)
    }

    @Test
    fun elLikePerteneceAlUsuarioQueInicioSesion() {
        val review = ReviewConUsuarioDto().copy(likesDeUsuarios = listOf("ana", "luis"))
        assertTrue(review.toSongReviewItemUi("ana").isLiked)
        assertFalse(review.toSongReviewItemUi("maria").isLiked)
        assertFalse(review.toSongReviewItemUi("").isLiked)
        assertEquals(2, review.toSongReviewItemUi("maria").likes)
    }

    @Test
    fun lasListasDeResenasMuestranElMismoTotalSinDuplicados() {
        val usuarios = listOf("ana", "luis", "ana")
        val detalle = ReviewConUsuarioDto().copy(likesDeUsuarios = usuarios)
            .toSongReviewItemUi("ana")
        val perfil = ReviewConAlbumDto().copy(likesDeUsuarios = usuarios).toMyReviewUi()
        assertEquals(2, detalle.likes)
        assertEquals(detalle.likes, perfil.likes)
    }

    @Test
    fun cadaSeguidorSeCuentaUnaVez() {
        val perfil = UsuarioPerfilFirestoreDto().copy(seguidoresIds = listOf("ana", "ana", "luis"))
        assertEquals(2, perfil.toUsuarioUi().seguidores)
    }
}
