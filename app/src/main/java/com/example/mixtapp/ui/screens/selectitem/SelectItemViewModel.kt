package com.example.mixtapp.ui.screens.selectitem

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.ui.screens.selectitem.model.FILTRO_TODOS
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
class SelectItemViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(SelectItemState())
    val uiState: StateFlow<SelectItemState> = _uiState.asStateFlow()

    // No recibe parametros: el catalogo en vivo se escucha una sola vez al crear el ViewModel
    init {
        escucharCatalogo()
    }

    // Si alguien califica algo mientras elijo, las estrellas de la lista se actualizan solas
    private fun escucharCatalogo() {
        viewModelScope.launch(ioDispatcher) {
            catalogoRepository.getCatalogoLive()
                .catch { e ->
                    Log.d("SelectItemViewModel", "escucharCatalogo: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { catalogo ->
                    _uiState.update {
                        it.copy(
                            catalogo = catalogo,
                            resultados = filtrar(texto = it.query, filtroId = it.selectedFilterId, catalogo = catalogo),
                            isLoading = false,
                            errorMessageRes = null,
                        )
                    }
                }
        }
    }

    // Cada letra filtra el catalogo que ya esta cargado: no se vuelve a pedir
    fun updateQuery(texto: String) {
        _uiState.update {
            it.copy(
                query = texto,
                resultados = filtrar(texto = texto, filtroId = it.selectedFilterId, catalogo = it.catalogo),
            )
        }
    }

    // Todos o solo un tipo: albumes, canciones o artistas
    fun updateSelectedFilter(filtroId: String) {
        _uiState.update {
            it.copy(
                selectedFilterId = filtroId,
                resultados = filtrar(texto = it.query, filtroId = filtroId, catalogo = it.catalogo),
            )
        }
    }

    // Los del tipo elegido cuyo titulo, subtitulo o detalle contienen el texto (sin importar las
    // mayusculas), los mas populares primero
    private fun filtrar(texto: String, filtroId: String, catalogo: List<ItemConResenasUi>): List<ItemConResenasUi> {
        val buscado = texto.trim()

        return catalogo.filter { filtroId == FILTRO_TODOS || it.item.tipo == filtroId }
            .filter {
                buscado.isEmpty() ||
                    it.item.titulo.contains(buscado, ignoreCase = true) ||
                    it.item.subtitulo.contains(buscado, ignoreCase = true) ||
                    it.item.detalle.contains(buscado, ignoreCase = true)
            }
            .sortedByDescending { it.popularidad }
    }

}
