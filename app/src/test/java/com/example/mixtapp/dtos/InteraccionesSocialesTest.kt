package com.example.mixtapp.dtos

import com.example.mixtapp.data.dtos.ReviewDto
import com.example.mixtapp.data.dtos.UsuarioPerfilFirestoreDto
import com.example.mixtapp.data.dtos.toReviewUi
import com.google.common.truth.Truth.assertThat
import org.junit.Test

// Los contadores y las marcas de likes y seguidores salen del documento al mapear
class InteraccionesSocialesTest {

    @Test
    fun toReviewUi_documentoSinContadores_muestraCero() {
        //Arrange
        val dto = ReviewDto()
        //Act
        val review = dto.toReviewUi(usuarioActualId = "usuario")
        //Assert
        assertThat(review.likes).isEqualTo(0)
        assertThat(review.likesText).isEqualTo("0")
        assertThat(review.isLiked).isFalse()
    }

    @Test
    fun toUsuarioUi_documentoSinContadores_muestraCero() {
        //Arrange
        val dto = UsuarioPerfilFirestoreDto()
        //Act
        val perfil = dto.toUsuarioUi()
        //Assert
        assertThat(perfil.seguidores).isEqualTo(0)
        assertThat(perfil.seguidos).isEqualTo(0)
        assertThat(perfil.siguiendo).isFalse()
    }

    @Test
    fun toReviewUi_conLikesCount_esElTotalDeLikes() {
        //Arrange
        val dto = ReviewDto().copy(likesCount = 3)
        //Act
        val review = dto.toReviewUi(usuarioActualId = "ana")
        //Assert
        assertThat(review.likes).isEqualTo(3)
    }

    @Test
    fun toReviewUi_conMiLike_quedaMarcado() {
        //Arrange
        val dto = ReviewDto().copy(liked = true)
        //Act
        val review = dto.toReviewUi(usuarioActualId = "ana")
        //Assert
        assertThat(review.isLiked).isTrue()
    }

    @Test
    fun toReviewUi_sinMiLike_noQuedaMarcado() {
        //Arrange
        val dto = ReviewDto()
        //Act
        val review = dto.toReviewUi(usuarioActualId = "ana")
        //Assert
        assertThat(review.isLiked).isFalse()
    }

    @Test
    fun toReviewUi_resenaDelUsuarioDeLaApp_esMia() {
        //Arrange
        val dto = ReviewDto().copy(usuarioId = "ana")
        //Act
        val review = dto.toReviewUi(usuarioActualId = "ana")
        //Assert
        assertThat(review.isMine).isTrue()
    }

    @Test
    fun toReviewUi_resenaDeOtroUsuario_noEsMia() {
        //Arrange
        val dto = ReviewDto().copy(usuarioId = "luis")
        //Act
        val review = dto.toReviewUi(usuarioActualId = "ana")
        //Assert
        assertThat(review.isMine).isFalse()
    }

    @Test
    fun toUsuarioUi_conContadoresDeSeguidores_salenDelDocumento() {
        //Arrange
        val dto = UsuarioPerfilFirestoreDto().copy(followersCount = 2, followingCount = 5, followed = true)
        //Act
        val perfil = dto.toUsuarioUi()
        //Assert
        assertThat(perfil.seguidores).isEqualTo(2)
        assertThat(perfil.seguidos).isEqualTo(5)
        assertThat(perfil.siguiendo).isTrue()
    }
}
