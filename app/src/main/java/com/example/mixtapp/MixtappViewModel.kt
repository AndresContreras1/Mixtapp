package com.example.mixtapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.data.repository.AlbumRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MixtappViewModel @Inject constructor(
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MixtappState())
    val uiState: StateFlow<MixtappState> = _uiState.asStateFlow()

    init {
        getAlbumParaResenar()
    }

    private fun getAlbumParaResenar() {
        viewModelScope.launch {
            val result = albumRepository.getAlbumPorDefecto()

            if (result.isSuccess) {
                _uiState.update { it.copy(albumParaResenar = result.getOrNull()?.id ?: "") }
            }
        }
    }
}
