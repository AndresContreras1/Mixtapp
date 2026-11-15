package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.UbicacionDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.ComentarioDto
import com.example.mixtapp.data.dtos.CreateReviewDto
import com.example.mixtapp.data.dtos.CreateReviewItemDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.UbicacionDto
import com.example.mixtapp.data.local.LocalPreviewProvider
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
    private val ubicacionDataSource = mockk<UbicacionDataSource>()

    private val repository = ReviewRepository(reviewDataSource, usuarioDataSource, authDataSource, ubicacionDataSource)

    // La resena que se publica: el album de ejemplo con 5 estrellas, a una hora fija
    private val item = LocalPreviewProvider.itemConResenas.item
    private val ahora = Instant.parse("2026-11-14T15:00:00Z")

    // Lo que el repositorio deberia mandar al data source; cada prueba cambia lo que le toca
    private fun resenaEsperada(createdAt: String?, latitud: Double?, longitud: Double?) = CreateReviewDto(
        usuarioId = "yo",
        itemId = item.id,
        tipo = item.tipo,
        calificacion = 5,
        comentario = "Muy bueno",
        fechaEscucha = null,
        likesCount = 0,
        commentsCount = 0,
        createdAt = createdAt,
        updatedAt = ahora.toString(),
        usuario = CreateReviewUsuarioDto(nombre = "yo", fotoUrl = null),
        item = CreateReviewItemDto(titulo = item.titulo, subtitulo = item.subtitulo, imagenUrl = item.imagenUrl),
        latitud = latitud,
        longitud = longitud,
    )

    // Todas las pruebas son con la sesion iniciada; el usuario no tiene documento, asi que su nombre
    // sale del correo
    @Before
    fun setUp() {
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { authDataSource.currentUser?.photoUrl } returns null
        coEvery { authDataSource.currentUser?.email } returns "yo@example.com"
        coEvery { usuarioDataSource.getUsuarioById("yo", any()) } returns null
    }

    @Test
    fun guardarResena_nueva_guardaDondeEstoy() = runTest {
        //Arrange: el celular esta en Bogota. Si el DTO no llega con la ubicacion, el mock no coincide
        // y el repositorio responde con error
        coEvery { ubicacionDataSource.getUbicacionActual() } returns UbicacionDto(latitud = 4.65, longitud = -74.06)
        coEvery {
            reviewDataSource.createReview(resenaEsperada(createdAt = ahora.toString(), latitud = 4.65, longitud = -74.06))
        } returns Unit
        //Act
        val result = repository.guardarResena(item, 5, "Muy bueno", "", null, ahora)
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun guardarResena_sinPermisoDeUbicacion_seGuardaSinUbicacion() = runTest {
        //Arrange: sin permiso el data source retorna null
        coEvery { ubicacionDataSource.getUbicacionActual() } returns null
        coEvery {
            reviewDataSource.createReview(resenaEsperada(createdAt = ahora.toString(), latitud = null, longitud = null))
        } returns Unit
        //Act
        val result = repository.guardarResena(item, 5, "Muy bueno", "", null, ahora)
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun guardarResena_editar_noPideUbicacionNiCambiaLaFechaDeCreacion() = runTest {
        //Arrange: la ubicacion no se moquea; si el repositorio la pidiera, la prueba fallaria
        coEvery {
            reviewDataSource.updateReview("r1", resenaEsperada(createdAt = null, latitud = null, longitud = null))
        } returns Unit
        //Act
        val result = repository.guardarResena(item, 5, "Muy bueno", "", "r1", ahora)
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
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
        //Arrange: el data source ya trae solo las de las ultimas 24 horas, con mis likes
        val horaDelMapa = Instant.parse("2026-11-13T12:00:00Z")
        coEvery { reviewDataSource.listenReviewsDesde("2026-11-12T12:00:00Z", "yo") } returns flowOf(
            listOf(
                ReviewDto().copy(id = "vieja", createdAt = "2026-11-13T08:00:00Z", latitud = 4.6, longitud = -74.0),
                ReviewDto().copy(id = "sin_ubicacion", createdAt = "2026-11-13T09:00:00Z"),
                ReviewDto().copy(id = "nueva", createdAt = "2026-11-13T11:00:00Z", latitud = 6.2, longitud = -75.5),
            )
        )
        //Act
        val resenas = repository.getResenasEnElMapaLive(reloj = { horaDelMapa }).first()
        //Assert: la mas nueva primero, con sus coordenadas
        Truth.assertThat(resenas.map { it.review.id }).containsExactly("nueva", "vieja").inOrder()
        Truth.assertThat(resenas[0].latitud).isEqualTo(6.2)
        Truth.assertThat(resenas[0].longitud).isEqualTo(-75.5)
    }

    @Test
    fun getResenasEnElMapaLive_pasaronLas24Horas_saleDelMapa() = runTest {
        //Arrange: el listener empezo a las 12:00 del 13 y la siguiente emision llega al otro dia a
        // las 10:00; para entonces la de las 08:00 del 13 ya tiene mas de 24 horas
        var hora = Instant.parse("2026-11-13T12:00:00Z")
        coEvery { reviewDataSource.listenReviewsDesde("2026-11-12T12:00:00Z", "yo") } returns flowOf(
            listOf(
                ReviewDto().copy(id = "vieja", createdAt = "2026-11-13T08:00:00Z", latitud = 4.6, longitud = -74.0),
                ReviewDto().copy(id = "nueva", createdAt = "2026-11-14T09:00:00Z", latitud = 6.2, longitud = -75.5),
            )
        )
        //Act
        val flujo = repository.getResenasEnElMapaLive(reloj = { hora })
        hora = Instant.parse("2026-11-14T10:00:00Z")
        val resenas = flujo.first()
        //Assert
        Truth.assertThat(resenas.map { it.review.id }).containsExactly("nueva")
    }

    @Test
    fun getResenasEnElMapaLive_resenaConMiLike_corazonLleno() = runTest {
        //Arrange: el data source marca liked con mi like en la subcoleccion
        coEvery { reviewDataSource.listenReviewsDesde(any(), "yo") } returns flowOf(
            listOf(
                ReviewDto().copy(id = "r1", createdAt = "2026-11-13T11:00:00Z", latitud = 4.6, longitud = -74.0, liked = true),
            )
        )
        //Act
        val resenas = repository.getResenasEnElMapaLive(reloj = { Instant.parse("2026-11-13T12:00:00Z") }).first()
        //Assert
        Truth.assertThat(resenas.single().review.isLiked).isTrue()
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
