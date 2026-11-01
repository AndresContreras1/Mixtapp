package com.example.mixtapp.ui.screens.selectalbum

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.ui.utils.mensajeDeError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectAlbumViewModel @Inject constructor(
    private val albumRepository: AlbumRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectAlbumState())
    val uiState: StateFlow<SelectAlbumState> = _uiState

    // No recibe parametros, asi que los albumes se cargan al crear el ViewModel
    init {
        getAlbumes()
    }

    private fun getAlbumes() {
        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            val result = albumRepository.getAlbumReviews()

            if (result.isSuccess) {
                _uiState.update {
                    it.copy(
                        albums = result.getOrNull() ?: emptyList(),
                        isLoading = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isLoading = false, errorMessageRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_cargar_contenido))
                }
            }
        }
    }

}
