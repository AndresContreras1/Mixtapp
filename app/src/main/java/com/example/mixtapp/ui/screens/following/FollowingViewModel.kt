package com.example.mixtapp.ui.screens.following

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.ui.utils.mensajeDeError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FollowingViewModel @Inject constructor(
    private val socialRepository: SocialRepository,
    private val reviewRepository: ReviewRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(FollowingState())
    val uiState: StateFlow<FollowingState> = _uiState.asStateFlow()

    // La pestana es en vivo y no depende de la ruta: se escucha una sola vez al crear el ViewModel.
    // Si se pidiera en cada LaunchedEffect se acumularian listeners
    init {
        escucharSeguidos()
    }

    // Una resena nueva, un like o seguir a alguien llegan solos, sin salir y volver
    private fun escucharSeguidos() {
        viewModelScope.launch(ioDispatcher) {
            socialRepository.getFollowingLive()
                .catch { e ->
                    Log.d("FollowingViewModel", "escucharSeguidos: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { following ->
                    _uiState.update {
                        it.copy(
                            following = following,
                            // Al llegar datos nuevos se conserva lo que estaba escrito en el filtro
                            reviews = filtrarPorAutor(friendQuery = it.friendQuery, todas = following.reviews),
                            isLoading = false,
                            errorMessageRes = null,
                        )
                    }
                }
        }
    }

    // Las resenas ya estan cargadas: filtrar no las vuelve a pedir
    fun updateFriendQuery(friendQuery: String) {
        val todas = _uiState.value.following?.reviews ?: emptyList()

        _uiState.update {
            it.copy(
                friendQuery = friendQuery,
                reviews = filtrarPorAutor(friendQuery = friendQuery, todas = todas),
            )
        }
    }

    // No se copia el estado a mano: el like dispara el listener y llega la resena con el corazon y
    // el contador al dia
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_me_gusta,
                        )
                    )
                }
            }
        }
    }

    // Filtrar es logica de negocio, no de la pantalla. Con el campo vacio pasan todas
    private fun filtrarPorAutor(friendQuery: String, todas: List<ReviewUi>): List<ReviewUi> =
        todas.filter { it.author.contains(friendQuery.trim(), ignoreCase = true) }
}
