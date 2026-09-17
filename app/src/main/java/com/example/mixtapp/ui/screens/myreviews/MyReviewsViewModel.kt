package com.example.mixtapp.ui.screens.myreviews

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SocialRepository
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_A_Z
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_CALIFICACION_4
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_CALIFICACION_5
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_MEJOR_CALIFICADAS
import com.example.mixtapp.ui.screens.myreviews.model.myReviewFilters
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val socialRepository: SocialRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsState())
    val uiState: StateFlow<MyReviewsState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getReviews()
    }

    private fun getReviews() {
        val usuario = authRepository.currentUser?.email?.substringBefore("@") ?: ""
        val filtroInicial = myReviewFilters.first().id

        viewModelScope.launch {
            val perfil = socialRepository.getProfile()
            val resenas = reviewRepository.getMyReviews()

            if (perfil.isSuccess && resenas.isSuccess) {
                _uiState.update {
                    it.copy(
                        username = usuario,
                        joinDate = perfil.getOrNull()?.joinDate ?: "",
                        filters = myReviewFilters,
                        selectedFilterId = filtroInicial,
                        reviews = aplicarFiltro(
                            filtroId = filtroInicial,
                            todas = resenas.getOrNull() ?: emptyList(),
                        ),
                    )
                }
            }
        }
    }

    fun updateSelectedFilter(filtroId: String) {
        viewModelScope.launch {
            val resenas = reviewRepository.getMyReviews()

            if (resenas.isSuccess) {
                _uiState.update {
                    it.copy(
                        selectedFilterId = filtroId,
                        reviews = aplicarFiltro(
                            filtroId = filtroId,
                            todas = resenas.getOrNull() ?: emptyList(),
                        ),
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
            else -> todas
        }
    }
}
