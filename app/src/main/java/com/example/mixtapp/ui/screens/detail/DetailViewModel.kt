package com.example.mixtapp.ui.screens.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.MixtapeRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.model.toItemResumenUi
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
class DetailViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    private val reviewRepository: ReviewRepository,
    private val mixtapeRepository: MixtapeRepository,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailState())
    val uiState: StateFlow<DetailState> = _uiState.asStateFlow()

    private var escuchando = false
    private var escuchandoMixtapes = false

    fun getItem(tipo: String, itemId: String) {
        if (escuchando) return
        escuchando = true

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            catalogoRepository.getItemLive(tipo = tipo, itemId = itemId)
                .catch { e ->
                    Log.d("DetailViewModel", "getItem: ${e.message}")
                    escuchando = false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(
                                error = e,
                                generico = R.string.error_cargar_contenido,
                                noEncontrado = R.string.album_no_encontrado,
                            ),
                        )
                    }
                }
                .collect { itemConResenas ->
                    _uiState.update {
                        it.copy(itemConResenas = itemConResenas, isLoading = false, errorMessageRes = null)
                    }
                }
        }
    }

    fun sendOrDeleteLikeItem() {
        val item = _uiState.value.itemConResenas?.item ?: return

        viewModelScope.launch(ioDispatcher) {
            val result = catalogoRepository.sendOrDeleteLike(item = item)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_me_gusta,
                            noEncontrado = R.string.album_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    fun sendOrDeleteGuardado() {
        val item = _uiState.value.itemConResenas?.item ?: return

        viewModelScope.launch(ioDispatcher) {
            val result = catalogoRepository.sendOrDeleteGuardado(item = item)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_guardar_item,
                            noEncontrado = R.string.album_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    fun sendOrDeleteLikeResena(reviewId: String) {
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
                            noEncontrado = R.string.album_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    fun eliminarMiResena(reviewId: String) {
        if (_uiState.value.eliminando) return

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(eliminando = true, errorAccionRes = null) }

            val result = reviewRepository.eliminarResena(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(eliminando = false, errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        eliminando = false,
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_eliminar_resena,
                            noEncontrado = R.string.album_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    fun setShowAddToMixtapeDialog(show: Boolean) {
        _uiState.update { it.copy(showAddToMixtapeDialog = show) }
        if (show && !escuchandoMixtapes) {
            escuchandoMixtapes = true
            viewModelScope.launch(ioDispatcher) {
                mixtapeRepository.obtenerMisMixtapes()
                    .catch { e ->
                        Log.d("DetailViewModel", "obtenerMisMixtapes: ${e.message}")
                        escuchandoMixtapes = false
                    }
                    .collect { mixtapes ->
                        _uiState.update { it.copy(misMixtapes = mixtapes) }
                    }
            }
        }
    }

    fun agregarItemAMixtape(mixtapeId: String) {
        val item = _uiState.value.itemConResenas?.item ?: return
        viewModelScope.launch(ioDispatcher) {
            val result = mixtapeRepository.agregarItemAMixtape(mixtapeId = mixtapeId, item = item.toItemResumenUi())
            if (result.isSuccess) {
                _uiState.update { it.copy(showAddToMixtapeDialog = false, errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        showAddToMixtapeDialog = false,
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_guardar_item,
                        )
                    )
                }
            }
        }
    }
}
