package com.example.mixtapp.ui.screens.myreviews

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_A_Z
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_CALIFICACION_4
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_CALIFICACION_5
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_MEJOR_CALIFICADAS
import com.example.mixtapp.ui.screens.myreviews.model.myReviewFilters
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
class MyReviewsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reviewRepository: ReviewRepository,
    private val usuarioRepository: UsuarioRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsState())
    val uiState: StateFlow<MyReviewsState> = _uiState.asStateFlow()

    // El flujo de mis resenas se escucha mientras exista el ViewModel, una sola vez
    private var escuchandoResenas = false

    // La pantalla la llama cada vez que se muestra: mi usuario se pide de nuevo y mis resenas
    // llegan en vivo, asi que al volver de editar ya salen al dia
    fun getReviews() {
        val usuarioDelCorreo = authRepository.usuarioDelCorreo

        // Mis datos y mis resenas se usan por su lado, asi que cada uno va en su propio launch
        viewModelScope.launch(ioDispatcher) {
            val perfil = usuarioRepository.getMiPerfil()

            _uiState.update {
                // Sin mis datos en Firestore se queda el usuario que ya tenia, o la parte del correo
                val usuario = perfil.getOrNull()?.username ?: it.username.ifEmpty { usuarioDelCorreo }

                it.copy(username = usuario, iniciales = usuario.take(2).uppercase())
            }
        }

        if (escuchandoResenas) return
        escuchandoResenas = true

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            reviewRepository.getMyReviewsLive()
                .catch { e ->
                    Log.d("MyReviewsViewModel", "getReviews: ${e.message}")
                    escuchandoResenas = false
                    _uiState.update {
                        it.copy(
                            filters = myReviewFilters,
                            selectedFilterId = it.selectedFilterId.ifEmpty { myReviewFilters.first().id },
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { todas ->
                    _uiState.update {
                        // Al llegar datos nuevos se conserva el filtro que estaba elegido
                        val filtroId = it.selectedFilterId.ifEmpty { myReviewFilters.first().id }

                        it.copy(
                            todas = todas,
                            filters = myReviewFilters,
                            selectedFilterId = filtroId,
                            reviews = aplicarFiltro(filtroId = filtroId, todas = todas),
                            isLoading = false,
                            errorMessageRes = null,
                        )
                    }
                }
        }
    }

    // Las resenas ya estan cargadas: filtrar no vuelve a pedirlas
    fun updateSelectedFilter(filtroId: String) {
        val todas = _uiState.value.todas

        _uiState.update {
            it.copy(
                selectedFilterId = filtroId,
                reviews = aplicarFiltro(filtroId = filtroId, todas = todas),
                errorMessageRes = null,
            )
        }
    }

    // Eliminar filtra la lista en vez de volver a pedir todas las resenas
    fun eliminarResena(reviewId: String) {
        if (_uiState.value.eliminando) return

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(eliminando = true, errorMessageRes = null) }

            val result = reviewRepository.eliminarResena(reviewId = reviewId)

            if (result.isSuccess) {
                val restantes = _uiState.value.todas.filter { it.id != reviewId }

                _uiState.update {
                    it.copy(
                        todas = restantes,
                        reviews = aplicarFiltro(filtroId = it.selectedFilterId, todas = restantes),
                        eliminando = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        eliminando = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_eliminar_resena,
                        )
                    )
                }
            }
        }
    }

    // Ordenar y filtrar es logica de negocio, no de la pantalla
    private fun aplicarFiltro(filtroId: String, todas: List<MyReviewUi>): List<MyReviewUi> {
        return when (filtroId) {
            FILTRO_MEJOR_CALIFICADAS -> todas.sortedByDescending { it.rating }
            FILTRO_A_Z -> todas.sortedBy { it.album.title }
            FILTRO_CALIFICACION_5 -> todas.filter { it.rating == 5 }
            FILTRO_CALIFICACION_4 -> todas.filter { it.rating == 4 }
            // Recientes: la fecha va como AAAA-MM-DD, asi que ordenar el texto ordena las fechas
            else -> todas.sortedByDescending { it.date }
        }
    }

}
