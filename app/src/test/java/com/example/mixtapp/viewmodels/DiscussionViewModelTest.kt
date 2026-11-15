package com.example.mixtapp.viewmodels

import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.ui.screens.discussion.DiscussionViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

// La discusion con el repositorio moqueado: la que no existe, a quien se responde y en que hilo
// queda una respuesta, y el comentario vacio
@OptIn(ExperimentalCoroutinesApi::class)
class DiscussionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var reviewRepository: ReviewRepository
    private lateinit var viewModel: DiscussionViewModel

    // comentario_1 es de Valentina; comentario_2 es la respuesta de Diego a comentario_1
    private val discussion = LocalPreviewProvider.discussion

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        reviewRepository = mockk()
        viewModel = DiscussionViewModel(reviewRepository, testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun abrirLaDiscusion() {
        every { reviewRepository.getDiscussionLive(discussion.id) } returns flowOf(discussion)
        viewModel.getDiscussionByReviewId(reviewId = discussion.id)
    }

    @Test
    fun getDiscussion_queNoExiste_errorDiscusionNoEncontrada() = runTest {
        //Arrange
        every { reviewRepository.getDiscussionLive("r999") } returns flow { throw ContenidoNoEncontradoException() }
        //Act
        viewModel.getDiscussionByReviewId(reviewId = "r999")
        advanceUntilIdle()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessageRes).isEqualTo(R.string.discusion_no_encontrada)
    }

    @Test
    fun responderComentario_unaRespuesta_respondiendoASuAutor() = runTest {
        //Arrange
        abrirLaDiscusion()
        advanceUntilIdle()
        //Act
        viewModel.responderComentario(commentId = "comentario_2")
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.replyingToCommentId).isEqualTo("comentario_2")
        assertThat(state.respondiendoA).isEqualTo("Diego Alfaro")
    }

    @Test
    fun publicarComentario_aUnaRespuesta_quedaEnElHiloDelPrincipal() = runTest {
        //Arrange: el mock solo responde con el comentario principal como padre
        abrirLaDiscusion()
        coEvery {
            reviewRepository.publicarComentario(reviewId = discussion.id, texto = "Yo tambien", parentCommentId = "comentario_1")
        } returns Result.success(Unit)
        advanceUntilIdle()
        viewModel.responderComentario(commentId = "comentario_2")
        viewModel.updateNuevoComentario("  Yo tambien ")
        //Act
        viewModel.publicarComentario()
        advanceUntilIdle()
        //Assert: el campo queda vacio y ya no se responde a nadie
        val state = viewModel.uiState.value
        assertThat(state.nuevoComentario).isEmpty()
        assertThat(state.respondiendoA).isNull()
        assertThat(state.errorAccionRes).isNull()
    }

    @Test
    fun publicarComentario_soloEspacios_errorComentarioVacio() = runTest {
        //Arrange
        abrirLaDiscusion()
        advanceUntilIdle()
        viewModel.updateNuevoComentario("   ")
        //Act
        viewModel.publicarComentario()
        advanceUntilIdle()
        //Assert
        assertThat(viewModel.uiState.value.errorAccionRes).isEqualTo(R.string.error_comentario_vacio)
    }

    @Test
    fun cancelarRespuesta_respondiendo_yaNoSeRespondeANadie() = runTest {
        //Arrange
        abrirLaDiscusion()
        advanceUntilIdle()
        viewModel.responderComentario(commentId = "comentario_1")
        //Act
        viewModel.cancelarRespuesta()
        //Assert
        val state = viewModel.uiState.value
        assertThat(state.replyingToCommentId).isNull()
        assertThat(state.respondiendoA).isNull()
    }
}
