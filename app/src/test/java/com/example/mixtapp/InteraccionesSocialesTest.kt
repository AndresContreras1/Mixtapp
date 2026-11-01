package com.example.mixtapp

import com.example.mixtapp.data.dtos.ReviewDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.dtos.toReviewUi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

// Los contadores y las marcas de likes y seguidores salen del documento al mapear
class InteraccionesSocialesTest {

    @Test
    fun documentoSinContadores_mapeo_muestraCero() {
        val review = ReviewDto().toReviewUi(usuarioActualId = "usuario")
        assertEquals(0, review.likes)
        assertEquals("0", review.likesText)
        assertFalse(review.isLiked)

        val perfil = UsuarioPerfilFirestoreDto().toUsuarioUi()
        assertEquals(0, perfil.seguidores)
        assertEquals(0, perfil.seguidos)
        assertFalse(perfil.siguiendo)
    }

    @Test
    fun likesCount_mapeo_esElTotalDeLikes() {
        val review = ReviewDto().copy(likesCount = 3).toReviewUi(usuarioActualId = "ana")

        assertEquals(3, review.likes)
    }

    @Test
    fun likeDelUsuario_mapeo_quedaMarcado() {
        assertTrue(ReviewDto().copy(liked = true).toReviewUi(usuarioActualId = "ana").isLiked)
        assertFalse(ReviewDto().toReviewUi(usuarioActualId = "ana").isLiked)
    }

    @Test
    fun resenaDelUsuarioDeLaApp_mapeo_esMia() {
        assertTrue(ReviewDto().copy(usuarioId = "ana").toReviewUi(usuarioActualId = "ana").isMine)
        assertFalse(ReviewDto().copy(usuarioId = "luis").toReviewUi(usuarioActualId = "ana").isMine)
    }

    @Test
    fun contadoresDeSeguidores_mapeo_salenDelDocumento() {
        val perfil = UsuarioPerfilFirestoreDto()
            .copy(followersCount = 2, followingCount = 5, followed = true)
            .toUsuarioUi()
        assertEquals(2, perfil.seguidores)
        assertEquals(5, perfil.seguidos)
        assertTrue(perfil.siguiendo)
    }
}
