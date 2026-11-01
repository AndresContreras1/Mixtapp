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
import com.example.mixtapp.ui.screens.home.model.FILTRO_DESCUBRE
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
// Desde esta calificacion mi resena cuenta como que el item me gusta
private const val CALIFICACION_QUE_ME_GUSTA = 3

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

    // Mis likes dicen que generos me gustan (junto con mis resenas, que llegan con el catalogo): con
    // ellos se arman Para ti, Descubre y Tendencias
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
    private fun armarInicio(estado: HomeState): HomeState {
        val meGustan = loQueMeGusta(catalogo = estado.catalogo, favoritos = estado.favoritos)
        // Cuanto pesa cada genero y cada subgenero: cuantos items de el me gustan
        val pesos = meGustan.groupBy { it.genero }.mapValues { (_, delGenero) -> delGenero.size }
        val afinidad = meGustan.groupBy { it.subgenero }.mapValues { (_, delSubgenero) -> delSubgenero.size }

        return estado.copy(
            enTendencia = enTendencia(catalogo = estado.catalogo, pesos = pesos),
            canciones = armarFila(tipo = TIPO_CANCION, estado = estado, pesos = pesos, afinidad = afinidad),
            albumes = armarFila(tipo = TIPO_ALBUM, estado = estado, pesos = pesos, afinidad = afinidad),
            artistas = armarFila(tipo = TIPO_ARTISTA, estado = estado, pesos = pesos, afinidad = afinidad),
            resenasDeSeguidos = resenasDeSeguidos(catalogo = estado.catalogo, seguidosIds = estado.seguidosIds),
        )
    }

    // Lo que me gusta: lo que tiene mi like y lo que resene con 3 estrellas o mas. Una resena con
    // mala nota no cuenta como que el genero me gusta
    private fun loQueMeGusta(catalogo: List<ItemConResenasUi>, favoritos: List<ItemResumenUi>): List<ItemUi> {
        val idsConMiLike = favoritos.map { it.id }.toSet()
        val conMiLike = catalogo.filter { it.item.id in idsConMiLike }
        val conMiResena = catalogo.filter { conResenas ->
            conResenas.reviews.any { it.isMine && it.rating >= CALIFICACION_QUE_ME_GUSTA }
        }

        return (conMiLike + conMiResena).map { it.item }.filter { it.genero.isNotEmpty() }
    }

    // El album en el puesto mas alto de la lista de mi genero favorito (el numero mas bajo); sin
    // generos, de todos. Si ninguno esta en la lista, el album mas popular
    private fun enTendencia(catalogo: List<ItemConResenasUi>, pesos: Map<String, Int>): ItemUi? {
        val albumes = catalogo.filter { it.item.tipo == TIPO_ALBUM }
        val generoFavorito = pesos.maxByOrNull { it.value }?.key
        val deMiGenero = albumes.filter { generoFavorito == null || it.item.genero == generoFavorito }
        val delTop = deMiGenero.filter { it.item.tendencia != null }.minByOrNull { it.item.tendencia ?: Int.MAX_VALUE }

        return (delTop ?: deMiGenero.maxByOrNull { it.popularidad } ?: albumes.maxByOrNull { it.popularidad })?.item
    }

    // Una fila tiene solo un tipo; el filtro elegido decide cuales y en que orden. Para ti, Descubre
    // y Tendencias ordenan a su manera y despues reparten los cupos entre mis generos
    private fun armarFila(
        tipo: String,
        estado: HomeState,
        pesos: Map<String, Int>,
        afinidad: Map<String, Int>,
    ): List<ItemUi> {
        val delTipo = estado.catalogo.filter { it.item.tipo == tipo }

        return when (estado.selectedFilterId) {
            FILTRO_SEGUIDOS -> deSeguidos(items = delTipo, seguidosIds = estado.seguidosIds)
            FILTRO_TENDENCIAS -> repartirPorGenero(ordenados = deTendencias(items = delTipo), pesos = pesos)
            FILTRO_DESCUBRE -> repartirPorGenero(
                ordenados = porDescubrir(items = delTipo, favoritos = estado.favoritos, afinidad = afinidad),
                pesos = pesos,
            )
            else -> repartirPorGenero(ordenados = delTipo.sortedByDescending { it.popularidad }, pesos = pesos)
        }
    }

    // Primero los que estan en la lista de su genero, del puesto 1 hacia abajo; despues los demas
    // por popularidad, para completar si mis generos tienen pocos en la lista
    private fun deTendencias(items: List<ItemConResenasUi>): List<ItemConResenasUi> {
        val enLaLista = items.filter { it.item.tendencia != null }.sortedBy { it.item.tendencia ?: Int.MAX_VALUE }
        val demas = items.filter { it.item.tendencia == null }.sortedByDescending { it.popularidad }

        return enLaLista + demas
    }

    // Lo que todavia no conozco: sin mi like ni mi resena. Primero mi subgenero (el que mas me
    // gusta), despues lo mas nuevo y despues lo mas popular. sortedByDescending conserva el orden
    // de los empates, asi que se ordena de la regla menos importante a la mas importante
    private fun porDescubrir(
        items: List<ItemConResenasUi>,
        favoritos: List<ItemResumenUi>,
        afinidad: Map<String, Int>,
    ): List<ItemConResenasUi> {
        val idsConMiLike = favoritos.map { it.id }.toSet()

        return items.filter { conResenas ->
            conResenas.item.id !in idsConMiLike && conResenas.reviews.none { it.isMine }
        }
            .sortedByDescending { it.popularidad }
            .sortedByDescending { it.item.fechaLanzamiento }
            .sortedByDescending { afinidad[it.item.subgenero] ?: 0 }
    }

    // Los que resenaron los usuarios que sigo, el de la resena mas reciente primero. Las fechas van
    // en formato ISO, asi que comparar el texto compara las fechas
    private fun deSeguidos(items: List<ItemConResenasUi>, seguidosIds: Set<String>): List<ItemUi> =
        items.filter { conResenas -> conResenas.reviews.any { it.authorId in seguidosIds } }
            .sortedByDescending { conResenas ->
                conResenas.reviews.filter { it.authorId in seguidosIds }.maxOf { it.updatedAt }
            }
            .take(ITEMS_POR_FILA)
            .map { it.item }

    // Cada genero que me gusta recibe una parte de los 10 cupos segun su peso. La fila intercala
    // los generos, el de mas peso primero, cada uno en el orden que llega. Si sobran cupos se
    // completa primero con mas de mis generos y solo despues con los demas. Sin generos, la fila
    // es el orden que llega
    private fun repartirPorGenero(ordenados: List<ItemConResenasUi>, pesos: Map<String, Int>): List<ItemUi> {
        if (pesos.isEmpty()) return ordenados.take(ITEMS_POR_FILA).map { it.item }

        // groupBy conserva el orden: cada genero queda en el orden que llego
        val porGenero = ordenados.groupBy { it.item.genero }
        val generos = pesos.keys.sortedByDescending { pesos[it] ?: 0 }
        val elegidos = intercalarGeneros(generos = generos, porGenero = porGenero, cupos = cuposPorGenero(pesos = pesos))
        val idsElegidos = elegidos.map { it.item.id }.toSet()
        val demasDeMisGeneros = ordenados.filter { it.item.id !in idsElegidos && it.item.genero in pesos }
        val otros = ordenados.filter { it.item.id !in idsElegidos && it.item.genero !in pesos }

        return (elegidos + demasDeMisGeneros + otros).take(ITEMS_POR_FILA).map { it.item }
    }

    // Los 10 cupos se reparten en proporcion al peso de cada genero, redondeando. Sin minimo: si
    // casi todo lo que me gusta es metal y una sola cosa es reggaeton, el reggaeton puede quedar
    // con uno o con ninguno
    private fun cuposPorGenero(pesos: Map<String, Int>): Map<String, Int> {
        val total = pesos.values.sum()

        return pesos.mapValues { (_, peso) -> (peso * ITEMS_POR_FILA.toDouble() / total).roundToInt() }
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
