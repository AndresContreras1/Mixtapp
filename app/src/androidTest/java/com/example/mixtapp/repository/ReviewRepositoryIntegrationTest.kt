package com.example.mixtapp.repository

import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.ReviewFirestoreDataSourceImpl
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.toItemUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SinSesionException
import com.example.mixtapp.generateAlbum
import com.example.mixtapp.limpiarBase
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// El repositorio por integracion: mapea los DTO a objetos de UI, arma el hilo de comentarios y
// traduce los errores a un Result. Usa Firestore y Authentication del emulador
class ReviewRepositoryIntegrationTest {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private lateinit var reviewRepository: ReviewRepository
    private lateinit var correo: String

    @Before
    fun setUp() = runTest {
        try {
            db.useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) {

        }
        try {
            auth.useEmulator("10.0.2.2", 9099)
        } catch (e: Exception) {

        }

        reviewRepository = ReviewRepository(
            ReviewFirestoreDataSourceImpl(db),
            UsuarioFirestoreDataSourceImpl(db),
            AuthRemoteDataSource(auth),
        )
        limpiarBase(db)
        cargarDatosDePrueba(db)

        // Una cuenta nueva en el Authentication del emulador: crearla tambien inicia la sesion.
        // No tiene documento en usuarios
        correo = "prueba${System.currentTimeMillis()}@example.com"
        auth.createUserWithEmailAndPassword(correo, "123456").await()
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
        // Si la prueba cerro la sesion se vuelve a entrar con la cuenta de prueba para poder
        // borrarla: el Authentication del emulador queda como estaba
        val user = auth.currentUser ?: auth.signInWithEmailAndPassword(correo, "123456").await().user
        user?.delete()?.await()
        auth.signOut()
    }

    @Test
    fun guardarResena_validAlbum_success() = runTest {
        //Act
        val result = reviewRepository.guardarResena(generateAlbum(1).toItemUi(), 5, "Muy bueno", "2025-01-01", null)
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun guardarResena_sinSesion_sinSesionException() = runTest {
        //Arrange
        auth.signOut()
        //Act
        val result = reviewRepository.guardarResena(generateAlbum(1).toItemUi(), 5, "Muy bueno", "2025-01-01", null)
        //Assert
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }

    @Test
    fun guardarResena_usuarioSinDocumento_copiaElCorreoYElItem() = runTest {
        //Arrange
        val usuarioId = auth.currentUser?.uid ?: ""
        //Act
        reviewRepository.guardarResena(generateAlbum(1).toItemUi(), 5, "Muy bueno", "2025-01-01", null)
        //Assert
        val review = UsuarioFirestoreDataSourceImpl(db).getReviewsDeUsuario(usuarioId).first()
        Truth.assertThat(review.usuario?.nombre).isEqualTo(correo.substringBefore("@"))
        Truth.assertThat(review.tipo).isEqualTo(TIPO_ALBUM)
        Truth.assertThat(review.item?.titulo).isEqualTo("Album 1")
    }

    @Test
    fun sendOrDeleteLike_sinSesion_sinSesionException() = runTest {
        //Arrange
        auth.signOut()
        //Act
        val result = reviewRepository.sendOrDeleteLike("review_1")
        //Assert
        Truth.assertThat(result.isFailure).isTrue()
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }

    @Test
    fun publicarComentario_validReview_commentInDiscussion() = runTest {
        //Act
        val result = reviewRepository.publicarComentario("review_1", "Buen comentario", null)
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
        // Los listeners pueden emitir primero la cache local, que no tiene lo escrito por la
        // transaccion: se espera a que lleguen el comentario y el contador del servidor
        val discussion = reviewRepository.getDiscussionLive("review_1")
            .first { it.comments.isNotEmpty() && it.review.comments > 0 }
        Truth.assertThat(discussion.comments.first().content).isEqualTo("Buen comentario")
        Truth.assertThat(discussion.review.comments).isEqualTo(1)
    }

    @Test
    fun getDiscussionLive_respuesta_quedaDebajoDeSuComentario() = runTest {
        //Arrange: la respuesta es la mas nueva, pero va debajo del comentario que responde
        val dataSource = ReviewFirestoreDataSourceImpl(db)
        dataSource.createComentario("review_1", comentario("A", null, "2025-01-01T00:00:00Z"))
        dataSource.createComentario("review_1", comentario("B", null, "2025-01-02T00:00:00Z"))
        val comentarioA = dataSource.listenComentarios("review_1")
            .first { lista -> lista.any { it.contenido == "A" } }.first { it.contenido == "A" }
        dataSource.createComentario("review_1", comentario("Respuesta a A", comentarioA.id, "2025-01-03T00:00:00Z"))
        //Act: se espera la discusion con los tres comentarios, no la que trae la cache local
        val discussion = reviewRepository.getDiscussionLive("review_1").first { it.comments.size == 3 }
        //Assert
        Truth.assertThat(discussion.comments.map { it.content })
            .containsExactly("A", "Respuesta a A", "B").inOrder()
        Truth.assertThat(discussion.comments[1].isReply).isTrue()
    }

    @Test
    fun getDiscussionLive_invalidId_contenidoNoEncontrado() = runTest {
        //Act
        val error = runCatching { reviewRepository.getDiscussionLive("review_999").first() }.exceptionOrNull()
        //Assert
        Truth.assertThat(error).isInstanceOf(ContenidoNoEncontradoException::class.java)
    }

    @Test
    fun eliminarResena_validReview_success() = runTest {
        //Act
        val result = reviewRepository.eliminarResena("review_1")
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
    }

    private fun comentario(texto: String, parentCommentId: String?, createdAt: String) = CreateComentarioDto(
        usuarioId = "usuario_2",
        contenido = texto,
        parentCommentId = parentCommentId,
        likesCount = 0,
        createdAt = createdAt,
    )
}
