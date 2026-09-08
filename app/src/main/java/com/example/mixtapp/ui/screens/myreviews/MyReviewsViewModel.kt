package com.example.mixtapp.ui.screens.myreviews

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import com.example.mixtapp.data.local.LocalMyReviewsProvider
import com.example.mixtapp.data.local.LocalProfileProvider
import com.example.mixtapp.ui.screens.myreviews.model.MyReviewUi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MyReviewsViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(MyReviewsState())
    val uiState: StateFlow<MyReviewsState> = _uiState.asStateFlow()

    // No recibe parametros, asi que los datos se cargan al crear el ViewModel
    init {
        getReviews()
    }

    private fun getReviews() {
        val perfil = LocalProfileProvider.profile
        val filtros = LocalMyReviewsProvider.filters
        val filtroInicial = filtros.first()

        _uiState.update {
            it.copy(
                username = perfil.username,
                joinDate = perfil.joinDate,
                filters = filtros,
                selectedFilter = filtroInicial,
                reviews = aplicarFiltro(filtro = filtroInicial),
            )
        }
    }

    fun updateSelectedFilter(filtro: String) {
        _uiState.update {
            it.copy(
                selectedFilter = filtro,
                reviews = aplicarFiltro(filtro = filtro),
            )
        }
    }

    // Ordenar y filtrar es logica de negocio, no de la pantalla
    private fun aplicarFiltro(filtro: String): List<MyReviewUi> {
        val todas = LocalMyReviewsProvider.reviews

        return when (filtro) {
            "Top Rated" -> todas.sortedByDescending { it.score }
            "A-Z" -> todas.sortedBy { it.title }
            "5" -> todas.filter { it.score == 5 }
            "4" -> todas.filter { it.score == 4 }
            else -> todas
        }
    }
}
