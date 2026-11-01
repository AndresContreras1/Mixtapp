package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.ComentarioDto
import com.example.mixtapp.data.dtos.ReviewDto
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ReviewRepository
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

// El repositorio de resenas con mocks: los flujos del data source se reemplazan por flowOf con lo
// que llegaria de Firestore, y se prueba como el repositorio los ordena y los mapea
class ReviewRepositoryTest {

    private val reviewDataSource = mockk<ReviewRemoteDataSource>()
    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()

    private val repository = ReviewRepository(reviewDataSource, usuarioDataSource, authDataSource)

    @Test
    fun getMyReviewsLive_tresResenas_laUltimaEditadaPrimeroYMias() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { usuarioDataSource.listenReviewsDeUsuario("yo", "yo") } returns flowOf(
            listOf(
                ReviewDto().copy(id = "vieja", usuarioId = "yo", updatedAt = "2026-10-01T00:00:00Z"),
                ReviewDto().copy(id = "nueva", usuarioId = "yo", updatedAt = "2026-10-20T00:00:00Z"),
                ReviewDto().copy(id = "media", usuarioId = "yo", updatedAt = "2026-10-10T00:00:00Z"),
            )
        )
        //Act
        val reviews = repository.getMyReviewsLive().first()
        //Assert
        Truth.assertThat(reviews.map { it.id }).containsExactly("nueva", "media", "vieja").inOrder()
        Truth.assertThat(reviews.all { it.isMine }).isTrue()
    }

    @Test
    fun getDiscussionLive_respuesta_quedaDebajoDeSuComentario() = runTest {
        //Arrange: los comentarios llegan del mas viejo al mas nuevo, y la respuesta es la ultima
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { reviewDataSource.listenReviewById("r1", "yo") } returns flowOf(ReviewDto().copy(id = "r1"))
        coEvery { reviewDataSource.listenComentarios("r1", "yo") } returns flowOf(
            listOf(
                ComentarioDto().copy(id = "a", contenido = "A", createdAt = "2026-10-01T00:00:00Z"),
                ComentarioDto().copy(id = "b", contenido = "B", createdAt = "2026-10-02T00:00:00Z"),
                ComentarioDto().copy(id = "c", contenido = "Respuesta a A", parentCommentId = "a", createdAt = "2026-10-03T00:00:00Z"),
            )
        )
        //Act
        val discussion = repository.getDiscussionLive("r1").first()
        //Assert
        Truth.assertThat(discussion.comments.map { it.content }).containsExactly("A", "Respuesta a A", "B").inOrder()
        Truth.assertThat(discussion.comments[1].isReply).isTrue()
    }

    @Test
    fun getDiscussionLive_resenaQueNoExiste_contenidoNoEncontrado() = runTest {
        //Arrange: el data source envia null cuando la resena no existe
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { reviewDataSource.listenReviewById("r999", "yo") } returns flowOf(null)
        coEvery { reviewDataSource.listenComentarios("r999", "yo") } returns flowOf(emptyList())
        //Act
        val error = runCatching { repository.getDiscussionLive("r999").first() }.exceptionOrNull()
        //Assert
        Truth.assertThat(error).isInstanceOf(ContenidoNoEncontradoException::class.java)
    }
}
