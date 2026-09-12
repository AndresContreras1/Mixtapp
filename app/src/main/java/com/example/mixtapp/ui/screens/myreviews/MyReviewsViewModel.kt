package com.example.mixtapp.ui.screens.myreviews

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.local.LocalProfileProvider
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_A_Z
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_CALIFICACION_4
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_CALIFICACION_5
import com.example.mixtapp.ui.screens.myreviews.model.FILTRO_MEJOR_CALIFICADAS
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewUi
import com.example.mixtapp.ui.screens.myreviews.model.myReviewFilters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MyReviewsViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsState())
    val uiState: StateFlow<MyReviewsState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getReviews()
    }

    private fun getReviews() {
        val usuario = authRepository.currentUser?.email?.substringBefore("@") ?: ""
        val perfil = LocalProfileProvider.profile
        val filtroInicial = myReviewFilters.first().id

        _uiState.update {
            it.copy(
                username = usuario,
                joinDate = perfil.joinDate,
                filters = myReviewFilters,
                selectedFilterId = filtroInicial,
                reviews = aplicarFiltro(filtroId = filtroInicial),
            )
        }
    }

    fun updateSelectedFilter(filtroId: String) {
        _uiState.update {
            it.copy(
                selectedFilterId = filtroId,
                reviews = aplicarFiltro(filtroId = filtroId),
            )
        }
    }

    // Ordenar y filtrar es logica de negocio, no de la pantalla
    private fun aplicarFiltro(filtroId: String): List<MyReviewUi> {
        val todas = LocalMyReviewsProvider.reviews

        return when (filtroId) {
            FILTRO_MEJOR_CALIFICADAS -> todas.sortedByDescending { it.rating }
            FILTRO_A_Z -> todas.sortedBy { it.title }
            FILTRO_CALIFICACION_5 -> todas.filter { it.rating == 5 }
            FILTRO_CALIFICACION_4 -> todas.filter { it.rating == 4 }
            else -> todas
        }
    }
}
