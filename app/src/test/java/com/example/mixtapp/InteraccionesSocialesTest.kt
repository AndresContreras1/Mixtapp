package com.example.mixtapp

import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.dtos.toMyReviewUi
import com.example.mixtapp.data.dtos.toAlbumReviewItemUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InteraccionesSocialesTest {

    @Test
    fun documentosAnterioresSinRelacionesMuestranCero() {
        val review = ReviewConUsuarioDto().toAlbumReviewItemUi("usuario")
        assertEquals(0, review.likes)
        assertFalse(review.isLiked)
        assertEquals(0, ReviewConAlbumDto().toMyReviewUi().likes)
        assertEquals(0, UsuarioPerfilFirestoreDto().toUsuarioUi().seguidores)
    }

    @Test
    fun elLikePerteneceAlUsuarioQueInicioSesion() {
        val review = ReviewConUsuarioDto().copy(likesDeUsuarios = listOf("ana", "luis"))
        assertTrue(review.toAlbumReviewItemUi("ana").isLiked)
        assertFalse(review.toAlbumReviewItemUi("maria").isLiked)
        assertFalse(review.toAlbumReviewItemUi("").isLiked)
        assertEquals(2, review.toAlbumReviewItemUi("maria").likes)
    }

    @Test
    fun lasListasDeResenasMuestranElMismoTotalSinDuplicados() {
        val usuarios = listOf("ana", "luis", "ana")
        val detalle = ReviewConUsuarioDto().copy(likesDeUsuarios = usuarios)
            .toAlbumReviewItemUi("ana")
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
