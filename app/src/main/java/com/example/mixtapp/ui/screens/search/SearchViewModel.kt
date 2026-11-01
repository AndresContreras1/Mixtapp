package com.example.mixtapp.ui.screens.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.CATEGORIA_FECHA_LANZAMIENTO
import com.example.mixtapp.data.model.CATEGORIA_MAS_POPULARES
import com.example.mixtapp.data.model.CATEGORIA_MEJOR_CALIFICADOS
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.UsuarioRepository
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

// Con menos letras la busqueda de personas traeria a casi todos
private const val LETRAS_PARA_BUSCAR_PERSONAS = 2

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    private val usuarioRepository: UsuarioRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchState())
    val uiState: StateFlow<SearchState> = _uiState.asStateFlow()

    // El catalogo es en vivo y se escucha una sola vez; las categorias no cambian, asi que se
    // cargan una vez al crear el ViewModel
    init {
        escucharCatalogo()
        getCategorias()
    }

    // Si alguien califica algo mientras busco, el promedio y el orden se actualizan solos
    private fun escucharCatalogo() {
        viewModelScope.launch(ioDispatcher) {
            catalogoRepository.getCatalogoLive()
                .catch { e ->
                    Log.d("SearchViewModel", "escucharCatalogo: ${e.message}")
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
                            // Si ya se escribio algo o se eligio una tarjeta, se resuelve con lo que llego
                            resultados = resultadosPara(texto = it.query, categoria = it.categoriaElegida, catalogo = catalogo),
                            isLoading = false,
                            errorMessageRes = null,
                        )
                    }
                }
        }
    }

    private fun getCategorias() {
        viewModelScope.launch(ioDispatcher) {
            val result = catalogoRepository.getSearchCategories()

            if (result.isSuccess) {
                val categorias = result.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        ordenes = categorias.filter { categoria -> !categoria.esGenero },
                        generos = categorias.filter { categoria -> categoria.esGenero },
                        errorCategoriasRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorCategoriasRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_cargar_contenido)
                    )
                }
            }
        }
    }

    // Cada letra filtra el catalogo que ya esta cargado; las personas se buscan en la base desde
    // 2 letras
    fun updateQuery(texto: String) {
        val buscarPersonas = texto.trim().length >= LETRAS_PARA_BUSCAR_PERSONAS

        _uiState.update {
            it.copy(
                query = texto,
                resultados = resultadosPara(texto = texto, categoria = it.categoriaElegida, catalogo = it.catalogo),
                usuarios = if (buscarPersonas) it.usuarios else emptyList(),
                errorUsuariosRes = null,
            )
        }

        if (buscarPersonas) buscarUsuarios(texto = texto)
    }

    // Abre la tarjeta de Explorar por o de Generos que se toco
    fun selectCategory(id: String) {
        _uiState.update {
            val categoria = (it.ordenes + it.generos).find { categoria -> categoria.id == id }

            it.copy(
                categoriaElegida = categoria,
                resultados = resultadosPara(texto = it.query, categoria = categoria, catalogo = it.catalogo),
            )
        }
    }

    // La X de la categoria vuelve a mostrar las tarjetas
    fun clearCategory() {
        _uiState.update {
            it.copy(
                categoriaElegida = null,
                resultados = resultadosPara(texto = it.query, categoria = null, catalogo = it.catalogo),
            )
        }
    }

    private fun buscarUsuarios(texto: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = usuarioRepository.buscarUsuarios(texto.trim())

            _uiState.update {
                // Si mientras llegaba la respuesta ya se escribio otra cosa, esta se descarta
                if (it.query != texto) {
                    it
                } else if (result.isSuccess) {
                    it.copy(usuarios = result.getOrNull() ?: emptyList(), errorUsuariosRes = null)
                } else {
                    it.copy(
                        usuarios = emptyList(),
                        errorUsuariosRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_cargar_contenido),
                    )
                }
            }
        }
    }

    // El texto manda sobre la categoria; sin texto ni categoria se ven las tarjetas y no hay lista
    private fun resultadosPara(
        texto: String,
        categoria: SearchCategoryUi?,
        catalogo: List<ItemConResenasUi>,
    ): List<ItemConResenasUi> = when {
        texto.isNotBlank() -> conTexto(texto = texto.trim(), catalogo = catalogo)
        categoria != null -> deCategoria(categoria = categoria, catalogo = catalogo)
        else -> emptyList()
    }

    // Los items cuyo titulo, subtitulo o detalle contienen el texto, sin importar las mayusculas
    private fun conTexto(texto: String, catalogo: List<ItemConResenasUi>): List<ItemConResenasUi> =
        catalogo.filter {
            it.item.titulo.contains(texto, ignoreCase = true) ||
                it.item.subtitulo.contains(texto, ignoreCase = true) ||
                it.item.detalle.contains(texto, ignoreCase = true)
        }.sortedByDescending { it.popularidad }

    private fun deCategoria(categoria: SearchCategoryUi, catalogo: List<ItemConResenasUi>): List<ItemConResenasUi> =
        when {
            // En los generos el id es el mismo texto que guarda la base en el campo genero
            categoria.esGenero -> catalogo.filter { it.item.genero == categoria.id }
                .sortedByDescending { it.popularidad }
            // Los artistas no tienen fecha. Va como AAAA-MM-DD: ordenar el texto ordena las fechas
            categoria.id == CATEGORIA_FECHA_LANZAMIENTO -> catalogo.filter { it.item.tipo != TIPO_ARTISTA }
                .sortedByDescending { it.item.fechaLanzamiento }
            categoria.id == CATEGORIA_MAS_POPULARES -> catalogo.sortedByDescending { it.popularidad }
            // Sin resenas no hay promedio que comparar
            categoria.id == CATEGORIA_MEJOR_CALIFICADOS -> catalogo.filter { it.ratingCount > 0 }
                .sortedByDescending { it.rating }
            else -> catalogo
        }

}
