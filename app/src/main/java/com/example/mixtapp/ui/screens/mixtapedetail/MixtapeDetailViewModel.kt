package com.example.mixtapp.ui.screens.mixtapedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.MixtapeRepository
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
class MixtapeDetailViewModel @Inject constructor(
    private val mixtapeRepository: MixtapeRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(MixtapeDetailState())
    val uiState: StateFlow<MixtapeDetailState> = _uiState.asStateFlow()

    private var escuchando = false

    fun escucharMixtape(id: String) {
        if (escuchando) return
        escuchando = true

        viewModelScope.launch(ioDispatcher) {
            mixtapeRepository.obtenerMixtapePorId(id)
                .catch { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = R.string.error_generico
                        )
                    }
                }
                .collect { mixtape ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            mixtape = mixtape,
                            errorMessageRes = if (mixtape == null) R.string.contenido_no_encontrado else null
                        )
                    }
                }
        }
    }

    fun eliminarItem(itemId: String) {
        val mixtapeId = _uiState.value.mixtape?.id ?: return
        viewModelScope.launch(ioDispatcher) {
            mixtapeRepository.eliminarItemDeMixtape(mixtapeId, itemId)
        }
    }

    fun eliminarMixtape(onDeleted: () -> Unit) {
        val mixtapeId = _uiState.value.mixtape?.id ?: return
        viewModelScope.launch(ioDispatcher) {
            val result = mixtapeRepository.eliminarMixtape(mixtapeId)
            if (result.isSuccess) {
                onDeleted()
            }
        }
    }
}
