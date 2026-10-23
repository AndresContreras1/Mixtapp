package com.example.mixtapp

import com.example.mixtapp.data.dtos.ReviewConAlbumDto
import com.example.mixtapp.data.dtos.ReviewConUsuarioDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.dtos.toAlbumReviewItemUi
import com.example.mixtapp.data.dtos.toMyReviewUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class InteraccionesSocialesTest {

    @Test
    fun documentosAnterioresSinContadoresMuestranCero() {
        val review = ReviewConUsuarioDto().toAlbumReviewItemUi("usuario")
        assertEquals(0, review.likes)
        assertFalse(review.isLiked)
        assertEquals(0, ReviewConAlbumDto().toMyReviewUi().likes)

        val perfil = UsuarioPerfilFirestoreDto().toUsuarioUi()
        assertEquals(0, perfil.seguidores)
        assertEquals(0, perfil.seguidos)
        assertFalse(perfil.siguiendo)
    }

    @Test
    fun elTotalDeLikesSaleDelContadorDelDocumento() {
        val detalle = ReviewConUsuarioDto().copy(likesCount = 3).toAlbumReviewItemUi("ana")
        val perfil = ReviewConAlbumDto().copy(likesCount = 3).toMyReviewUi()
        assertEquals(3, detalle.likes)
        assertEquals(detalle.likes, perfil.likes)
    }

    @Test
    fun elLikeDelUsuarioQueInicioSesionSeMarca() {
        assertTrue(ReviewConUsuarioDto().copy(liked = true).toAlbumReviewItemUi("ana").isLiked)
        assertFalse(ReviewConUsuarioDto().toAlbumReviewItemUi("ana").isLiked)
    }

    @Test
    fun losContadoresDeSeguidoresSalenDelDocumento() {
        val perfil = UsuarioPerfilFirestoreDto()
            .copy(followersCount = 2, followingCount = 5, followed = true)
            .toUsuarioUi()
        assertEquals(2, perfil.seguidores)
        assertEquals(5, perfil.seguidos)
        assertTrue(perfil.siguiendo)
    }
}
