package com.example.mixtapp.datasource

import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.conectarEmulador
import com.example.mixtapp.data.datasource.impl.firestore.ReviewFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.CreateComentarioDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.generateUsuario
import com.example.mixtapp.limpiarBase
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// Pruebas de integracion de las resenas y de sus comentarios contra el emulador de Firebase
class ReviewFirestoreDataSourceTest {

    private val db = Firebase.firestore

    private lateinit var dataSource: ReviewFirestoreDataSourceImpl

    @Before
    fun setUp() = runTest {
        conectarEmulador()

        dataSource = ReviewFirestoreDataSourceImpl(db)
        limpiarBase(db)
        cargarDatosDePrueba(db)
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
    }

    // Un comentario del usuario i, como lo arma el repositorio
    private fun generateComentario(i: Int, parentCommentId: String? = null): CreateComentarioDto {
        val usuario = generateUsuario(i)

        return CreateComentarioDto(
            usuarioId = usuario.id,
            contenido = "Comentario $i",
            parentCommentId = parentCommentId,
            likesCount = 0,
            createdAt = "2025-01-0${i}T00:00:00Z",
            usuario = CreateReviewUsuarioDto(nombre = usuario.nombre, fotoUrl = usuario.fotoUrl),
        )
    }

    @Test
    fun getReviewById_validId_correctReview() = runTest {
        //Arrange
        val id = "review_1"
        //Act
        val result = dataSource.getReviewById(id)
        //Assert
        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result?.id).isEqualTo(id)
        Truth.assertThat(result?.usuarioId).isEqualTo("usuario_1")
    }

    @Test
    fun getReviewById_invalidId_null() = runTest {
        //Arrange
        val id = "review_999"
        //Act
        val result = dataSource.getReviewById(id)
        //Assert
        Truth.assertThat(result).isNull()
    }

    @Test
    fun sendOrDeleteLike_likeReview_reviewLiked() = runTest {
        //Arrange
        val usuario = generateUsuario(2)
        //Act
        dataSource.sendOrDeleteLike("review_1", usuario.id)
        //Assert
        val result = dataSource.getReviewById("review_1", usuario.id)
        Truth.assertThat(result?.liked).isTrue()
    }

    @Test
    fun sendOrDeleteLike_likeReview_likesCountIncrease() = runTest {
        //Arrange
        val usuario = generateUsuario(2)
        val oldData = dataSource.getReviewById("review_1")
        //Act
        dataSource.sendOrDeleteLike("review_1", usuario.id)
        //Assert
        val result = dataSource.getReviewById("review_1")
        Truth.assertThat(result?.likesCount).isGreaterThan(oldData?.likesCount)
    }

    @Test
    fun createComentario_newComment_commentsCountIncrease() = runTest {
        //Arrange
        val oldData = dataSource.getReviewById("review_1")
        //Act
        dataSource.createComentario("review_1", generateComentario(2))
        //Assert
        val result = dataSource.getReviewById("review_1")
        Truth.assertThat(result?.commentsCount).isGreaterThan(oldData?.commentsCount)
    }

    @Test
    fun listenComentarios_twoComments_oldestFirst() = runTest {
        //Arrange
        dataSource.createComentario("review_1", generateComentario(3))
        dataSource.createComentario("review_1", generateComentario(2))
        //Act: el listener puede emitir primero la cache local, que no tiene lo escrito por la
        // transaccion, asi que se espera la lista con los dos comentarios
        val comentarios = dataSource.listenComentarios("review_1").first { it.size == 2 }
        //Assert
        Truth.assertThat(comentarios).hasSize(2)
        Truth.assertThat(comentarios.first().contenido).isEqualTo("Comentario 2")
    }

    @Test
    fun sendOrDeleteLikeComentario_likeComment_commentLiked() = runTest {
        //Arrange
        val usuario = generateUsuario(4)
        dataSource.createComentario("review_1", generateComentario(2))
        val comentarioId = dataSource.listenComentarios("review_1").first { it.isNotEmpty() }.first().id
        //Act
        dataSource.sendOrDeleteLikeComentario("review_1", comentarioId, usuario.id)
        //Assert: se espera a que llegue el contador del servidor y no el de la cache
        val comentario = dataSource.listenComentarios("review_1", usuario.id)
            .first { it.firstOrNull()?.likesCount == 1 }.first()
        Truth.assertThat(comentario.liked).isTrue()
        Truth.assertThat(comentario.likesCount).isEqualTo(1)
    }

    @Test
    fun deleteReview_withComments_commentsDeleted() = runTest {
        //Arrange
        dataSource.createComentario("review_1", generateComentario(2))
        //Act
        dataSource.deleteReview("review_1")
        //Assert
        val comentarios = db.collection("reviews").document("review_1")
            .collection("comments").get().await()
        Truth.assertThat(comentarios.isEmpty).isTrue()
        Truth.assertThat(dataSource.getReviewById("review_1")).isNull()
    }
}
