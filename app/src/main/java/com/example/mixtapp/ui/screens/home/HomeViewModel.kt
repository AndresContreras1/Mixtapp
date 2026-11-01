package com.example.mixtapp.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.home.model.FILTRO_SEGUIDOS
import com.example.mixtapp.ui.screens.home.model.FILTRO_TENDENCIAS
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
import kotlin.math.roundToInt

// Cada fila del inicio y la lista de resenas de seguidos muestran hasta 10
private const val ITEMS_POR_FILA = 10
private const val RESENAS_DE_SEGUIDOS = 10

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    private val usuarioRepository: UsuarioRepository,
    private val reviewRepository: ReviewRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    // Los tres flujos son en vivo y no reciben parametros: se escuchan una sola vez al crear el
    // ViewModel. Si se pidieran en cada LaunchedEffect se acumularian listeners
    init {
        escucharCatalogo()
        escucharFavoritos()
        escucharSeguidos()
    }

    // Cada vez que cambia un item, un like o una resena llega el catalogo de nuevo y se vuelve a
    // armar todo el inicio
    private fun escucharCatalogo() {
        viewModelScope.launch(ioDispatcher) {
            catalogoRepository.getCatalogoLive()
                .catch { e ->
                    Log.d("HomeViewModel", "escucharCatalogo: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { catalogo ->
                    _uiState.update {
                        armarInicio(estado = it.copy(catalogo = catalogo, isLoading = false, errorMessageRes = null))
                    }
                }
        }
    }

    // Mis likes dicen que generos me gustan: con ellos se arma Para ti
    private fun escucharFavoritos() {
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMisFavoritosLive()
                .catch { e ->
                    Log.d("HomeViewModel", "escucharFavoritos: ${e.message}")
                    _uiState.update {
                        it.copy(errorFavoritosRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido))
                    }
                }
                .collect { favoritos ->
                    _uiState.update {
                        armarInicio(estado = it.copy(favoritos = favoritos, errorFavoritosRes = null))
                    }
                }
        }
    }

    // Si sigo o dejo de seguir a alguien, el filtro de seguidos y las resenas ya cuentan con eso
    private fun escucharSeguidos() {
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMisSeguidosLive()
                .catch { e ->
                    Log.d("HomeViewModel", "escucharSeguidos: ${e.message}")
                    _uiState.update {
                        it.copy(errorSeguidosRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido))
                    }
                }
                .collect { seguidos ->
                    val seguidosIds = seguidos.map { usuario -> usuario.id }.toSet()

                    _uiState.update {
                        armarInicio(estado = it.copy(seguidosIds = seguidosIds, errorSeguidosRes = null))
                    }
                }
        }
    }

    // Las filas se arman con lo que ya llego: cambiar de filtro no vuelve a pedir nada
    fun updateSelectedFilter(filtroId: String) {
        _uiState.update { armarInicio(estado = it.copy(selectedFilterId = filtroId)) }
    }

    // El like de una resena de alguien que sigo. El contador cambia cuando llega el catalogo nuevo
    // por el listener: aqui no se copia nada a mano
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.sendOrDeleteLike(reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        errorAccionRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_me_gusta)
                    )
                }
            }
        }
    }

    // Con el catalogo, mis favoritos, mis seguidos y el filtro se arman la tarjeta, las tres filas
    // y las resenas. Se llama cada vez que cambia cualquiera de los cuatro
    private fun armarInicio(estado: HomeState): HomeState =
        estado.copy(
            enTendencia = enTendencia(catalogo = estado.catalogo),
            canciones = armarFila(tipo = TIPO_CANCION, estado = estado),
            albumes = armarFila(tipo = TIPO_ALBUM, estado = estado),
            artistas = armarFila(tipo = TIPO_ARTISTA, estado = estado),
            resenasDeSeguidos = resenasDeSeguidos(catalogo = estado.catalogo, seguidosIds = estado.seguidosIds),
        )

    // El album en el puesto mas alto del Top Colombia (el numero mas bajo); si ninguno esta en el
    // top, el album mas popular
    private fun enTendencia(catalogo: List<ItemConResenasUi>): ItemUi? {
        val albumes = catalogo.filter { it.item.tipo == TIPO_ALBUM }
        val delTop = albumes.filter { it.item.tendencia != null }.minByOrNull { it.item.tendencia ?: Int.MAX_VALUE }

        return (delTop ?: albumes.maxByOrNull { it.popularidad })?.item
    }

    // Una fila tiene solo un tipo; el filtro elegido decide cuales y en que orden
    private fun armarFila(tipo: String, estado: HomeState): List<ItemUi> {
        val delTipo = estado.catalogo.filter { it.item.tipo == tipo }

        return when (estado.selectedFilterId) {
            FILTRO_TENDENCIAS -> deTendencias(items = delTipo)
            FILTRO_SEGUIDOS -> deSeguidos(items = delTipo, seguidosIds = estado.seguidosIds)
            else -> paraTi(items = delTipo, favoritos = estado.favoritos)
        }
    }

    // Solo los que estan en el Top Colombia, del puesto 1 hacia abajo
    private fun deTendencias(items: List<ItemConResenasUi>): List<ItemUi> =
        items.filter { it.item.tendencia != null }
            .sortedBy { it.item.tendencia ?: Int.MAX_VALUE }
            .take(ITEMS_POR_FILA)
            .map { it.item }

    // Los que resenaron los usuarios que sigo, el de la resena mas reciente primero. Las fechas van
    // en formato ISO, asi que comparar el texto compara las fechas
    private fun deSeguidos(items: List<ItemConResenasUi>, seguidosIds: Set<String>): List<ItemUi> =
        items.filter { conResenas -> conResenas.reviews.any { it.authorId in seguidosIds } }
            .sortedByDescending { conResenas ->
                conResenas.reviews.filter { it.authorId in seguidosIds }.maxOf { it.updatedAt }
            }
            .take(ITEMS_POR_FILA)
            .map { it.item }

    // Para ti: cada genero de mis favoritos pesa cuantos favoritos tengo de el y recibe una parte de
    // los 10 cupos segun su peso (minimo 1). La fila intercala los generos, el de mas peso primero,
    // y si sobran cupos se completa con los demas items por popularidad. Sin favoritos, todo va
    // por popularidad
    private fun paraTi(items: List<ItemConResenasUi>, favoritos: List<ItemResumenUi>): List<ItemUi> {
        val porPopularidad = items.sortedByDescending { it.popularidad }
        val pesos = favoritos.filter { it.genero.isNotEmpty() }
            .groupBy { it.genero }
            .mapValues { (_, delGenero) -> delGenero.size }

        if (pesos.isEmpty()) return porPopularidad.take(ITEMS_POR_FILA).map { it.item }

        // groupBy conserva el orden: cada genero queda del mas popular al menos popular
        val porGenero = porPopularidad.groupBy { it.item.genero }
        val generos = pesos.keys.sortedByDescending { pesos[it] ?: 0 }
        val elegidos = intercalarGeneros(generos = generos, porGenero = porGenero, cupos = cuposPorGenero(pesos = pesos))
        val idsElegidos = elegidos.map { it.item.id }.toSet()
        val demas = porPopularidad.filter { it.item.id !in idsElegidos }

        return (elegidos + demas).take(ITEMS_POR_FILA).map { it.item }
    }

    // Los 10 cupos se reparten en proporcion al peso de cada genero, redondeando y con minimo 1
    private fun cuposPorGenero(pesos: Map<String, Int>): Map<String, Int> {
        val total = pesos.values.sum()

        return pesos.mapValues { (_, peso) ->
            maxOf(1, (peso * ITEMS_POR_FILA.toDouble() / total).roundToInt())
        }
    }

    // En cada vuelta entra el siguiente item de cada genero, mientras le queden cupo e items
    private fun intercalarGeneros(
        generos: List<String>,
        porGenero: Map<String, List<ItemConResenasUi>>,
        cupos: Map<String, Int>,
    ): List<ItemConResenasUi> {
        val elegidos = mutableListOf<ItemConResenasUi>()
        var vuelta = 0
        var entroAlguno = true

        while (entroAlguno && elegidos.size < ITEMS_POR_FILA) {
            entroAlguno = false

            generos.forEach { genero ->
                val delGenero = porGenero[genero] ?: emptyList()

                if (vuelta < (cupos[genero] ?: 0) && vuelta < delGenero.size) {
                    elegidos.add(delGenero[vuelta])
                    entroAlguno = true
                }
            }
            vuelta++
        }

        return elegidos
    }

    // Las resenas de quienes sigo (nunca las mias), la ultima publicada o editada primero
    private fun resenasDeSeguidos(catalogo: List<ItemConResenasUi>, seguidosIds: Set<String>): List<ReviewUi> =
        catalogo.flatMap { it.reviews }
            .filter { !it.isMine && it.authorId in seguidosIds }
            .sortedByDescending { it.updatedAt }
            .take(RESENAS_DE_SEGUIDOS)

}
