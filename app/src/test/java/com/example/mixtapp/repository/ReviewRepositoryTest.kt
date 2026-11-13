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
import java.time.Instant
import org.junit.Before
import org.junit.Test

// El repositorio de resenas con mocks: los flujos del data source se reemplazan por flowOf con lo
// que llegaria de Firestore, y se prueba como el repositorio los ordena y los mapea
class ReviewRepositoryTest {

    private val reviewDataSource = mockk<ReviewRemoteDataSource>()
    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()

    private val repository = ReviewRepository(reviewDataSource, usuarioDataSource, authDataSource)

    // Las tres pruebas son con la sesion iniciada
    @Before
    fun setUp() {
        coEvery { authDataSource.currentUser?.uid } returns "yo"
    }

    @Test
    fun getMyReviewsLive_tresResenas_laUltimaEditadaPrimeroYMias() = runTest {
        //Arrange
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
    fun getResenasEnElMapaLive_conYSinUbicacion_soloLasQueTienenUbicacion() = runTest {
        //Arrange: el data source ya trae solo las de las ultimas 24 horas
        val ahora = Instant.parse("2026-11-13T12:00:00Z")
        coEvery { reviewDataSource.listenReviewsDesde("2026-11-12T12:00:00Z") } returns flowOf(
            listOf(
                ReviewDto().copy(id = "vieja", createdAt = "2026-11-13T08:00:00Z", latitud = 4.6, longitud = -74.0),
                ReviewDto().copy(id = "sin_ubicacion", createdAt = "2026-11-13T09:00:00Z"),
                ReviewDto().copy(id = "nueva", createdAt = "2026-11-13T11:00:00Z", latitud = 6.2, longitud = -75.5),
            )
        )
        //Act
        val resenas = repository.getResenasEnElMapaLive(ahora = ahora).first()
        //Assert: la mas nueva primero, con sus coordenadas
        Truth.assertThat(resenas.map { it.review.id }).containsExactly("nueva", "vieja").inOrder()
        Truth.assertThat(resenas[0].latitud).isEqualTo(6.2)
        Truth.assertThat(resenas[0].longitud).isEqualTo(-75.5)
    }

    @Test
    fun getDiscussionLive_resenaQueNoExiste_contenidoNoEncontrado() = runTest {
        //Arrange: el data source envia null cuando la resena no existe
        coEvery { reviewDataSource.listenReviewById("r999", "yo") } returns flowOf(null)
        coEvery { reviewDataSource.listenComentarios("r999", "yo") } returns flowOf(emptyList())
        //Act
        val error = runCatching { repository.getDiscussionLive("r999").first() }.exceptionOrNull()
        //Assert
        Truth.assertThat(error).isInstanceOf(ContenidoNoEncontradoException::class.java)
    }
}
