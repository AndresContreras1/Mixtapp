package com.example.mixtapp.ui.screens.myreviews

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_A_Z
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_CALIFICACION_4
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_CALIFICACION_5
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_MEJOR_CALIFICADAS
import com.example.mixtapp.ui.screens.myreviews.model.myReviewFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyReviewsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val reviewRepository: ReviewRepository,
    private val usuarioRepository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsState())
    val uiState: StateFlow<MyReviewsState> = _uiState.asStateFlow()

    // La pantalla la llama cada vez que se muestra, para ver los cambios al volver de editar
    fun getReviews() {
        val usuarioDelCorreo = authRepository.currentUser?.email?.substringBefore("@") ?: ""
        // Al volver se conserva el filtro que estaba elegido
        val filtro = _uiState.value.selectedFilterId.ifEmpty { myReviewFilters.first().id }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            // Mis datos y mis resenas salen a la vez y se espera la mas lenta
            val perfilPendiente = async { usuarioRepository.getMiPerfil() }
            val resenasPendientes = async { reviewRepository.getMyReviews() }

            // Una cuenta sin datos en Firestore muestra la parte del correo
            val usuario = perfilPendiente.await().getOrNull()?.username ?: usuarioDelCorreo
            val resenas = resenasPendientes.await()

            if (resenas.isSuccess) {
                val todas = resenas.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        username = usuario,
                        iniciales = usuario.take(2).uppercase(),
                        todas = todas,
                        filters = myReviewFilters,
                        selectedFilterId = filtro,
                        reviews = aplicarFiltro(filtroId = filtro, todas = todas),
                        isLoading = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        username = usuario,
                        iniciales = usuario.take(2).uppercase(),
                        filters = myReviewFilters,
                        selectedFilterId = filtro,
                        isLoading = false,
                        errorMessageRes = mensajeDeError(
                            error = resenas.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
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
        viewModelScope.launch {
            val result = reviewRepository.eliminarResena(reviewId = reviewId)

            if (result.isSuccess) {
                val restantes = _uiState.value.todas.filter { it.id != reviewId }

                _uiState.update {
                    it.copy(
                        todas = restantes,
                        reviews = aplicarFiltro(filtroId = it.selectedFilterId, todas = restantes),
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
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

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}
