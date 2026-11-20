package com.example.mixtapp.ui.screens.resumen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.hayPocosDatos
import com.example.mixtapp.data.model.itemsQueMeGustan
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ReviewRepository
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
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ResumenViewModel @Inject constructor(
    private val reviewRepository: ReviewRepository,
    private val catalogoRepository: CatalogoRepository,
    private val usuarioRepository: UsuarioRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    // El año del resumen: solo cuentan las reseñas de este año
    private val anio: Int = LocalDate.now().year

    private val _uiState = MutableStateFlow(ResumenState())
    val uiState: StateFlow<ResumenState> = _uiState.asStateFlow()

    // Nada depende de la ruta: todo se pide una sola vez al crear el ViewModel. Mis reseñas, mis me
    // gusta y mis seguidores llegan en vivo, asi que si escribo una reseña o doy un me gusta el
    // resumen se recalcula solo. El catalogo se lee una vez: solo sirve para el genero y el artista
    init {
        _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }
        cargarCatalogo()
        escucharMisResenas()
        escucharFavoritos()
        escucharPerfil()
    }

    private fun cargarCatalogo() {
        viewModelScope.launch(ioDispatcher) {
            val result = catalogoRepository.getItemsDelCatalogo()
            val items = result.getOrNull()

            if (result.isSuccess && items != null) {
                _uiState.update { armarResumen(estado = it.copy(items = items)) }
            } else {
                Log.d("ResumenViewModel", "cargarCatalogo: ${result.exceptionOrNull()?.message}")
                _uiState.update { it.copy(isLoading = false, errorMessageRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_cargar_contenido)) }
            }
        }
    }

    // Solo mis reseñas: un listener sobre las mias, no sobre todas las de la app
    private fun escucharMisResenas() {
        viewModelScope.launch(ioDispatcher) {
            reviewRepository.getMyReviewsLive()
                .catch { e ->
                    Log.d("ResumenViewModel", "escucharMisResenas: ${e.message}")
                    _uiState.update {
                        it.copy(isLoading = false, errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido))
                    }
                }
                .collect { resenas ->
                    _uiState.update { armarResumen(estado = it.copy(misResenas = resenas, isLoading = false)) }
                }
        }
    }

    // Mis me gusta: si no llegan, el resumen se ve igual y avisa que ese numero puede estar incompleto
    private fun escucharFavoritos() {
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMisFavoritosLive()
                .catch { e ->
                    Log.d("ResumenViewModel", "escucharFavoritos: ${e.message}")
                    _uiState.update { it.copy(errorDatosRes = mensajeDeError(error = e, generico = R.string.error_datos_resumen)) }
                }
                .collect { favoritos ->
                    _uiState.update { armarResumen(estado = it.copy(favoritos = favoritos)) }
                }
        }
    }

    // Sin mi documento (cuenta nueva) los contadores quedan en 0: no es un error del resumen. Otro
    // error si se avisa
    private fun escucharPerfil() {
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMiPerfilLive()
                .catch { e ->
                    Log.d("ResumenViewModel", "escucharPerfil: ${e.message}")
                    if (e !is ContenidoNoEncontradoException) {
                        _uiState.update { it.copy(errorDatosRes = mensajeDeError(error = e, generico = R.string.error_datos_resumen)) }
                    }
                }
                .collect { perfil ->
                    _uiState.update { it.copy(seguidores = perfil.seguidores, seguidos = perfil.seguidos) }
                }
        }
    }

    // El boton: descubre la siguiente tarjeta y, cuando ya se vieron todas, vuelve a empezar
    fun avanzar() {
        _uiState.update {
            if (it.quedanPorVer) it.copy(reveladas = it.reveladas + 1) else it.copy(reveladas = 1)
        }
    }

    // Se llama cada vez que cambian mis reseñas, mis me gusta o llega el catalogo
    private fun armarResumen(estado: ResumenState): ResumenState {
        // Solo las reseñas de este año, cada una con el item que califique. updatedAt va en formato
        // ISO: los primeros cuatro caracteres son el año
        val itemsPorId = estado.items.associateBy { it.id }
        val delAnio = estado.misResenas.filter { it.updatedAt.take(4) == anio.toString() }
        val misResenas: List<Pair<ReviewUi, ItemUi>> = delAnio.mapNotNull { review ->
            itemsPorId[review.item.id]?.let { item -> review to item }
        }
        // Para el genero y el artista favoritos cada item cuenta una vez
        val meGustan = itemsQueMeGustan(
            items = estado.items,
            idsConMiLike = estado.favoritos.map { it.id }.toSet(),
            misCalificaciones = misResenas.associate { (review, item) -> item.id to review.rating },
        ).distinctBy { it.id }

        val mejor = misResenas.maxWithOrNull(compareBy({ it.first.rating }, { it.first.updatedAt }))
        val querida = misResenas.map { it.first }.filter { it.likes > 0 }.maxByOrNull { it.likes }
        val generoFavorito = masRepetido(meGustan.map { it.genero })
        val artistaFavorito = masRepetido(meGustan.map { it.artista })

        // La portada del genero: primero un album que me gusta de ese genero; si no, el mas popular
        val albumesDelGenero = estado.items.filter { it.tipo == TIPO_ALBUM && it.genero == generoFavorito }
        val portadaDelGenero = (
            meGustan.firstOrNull { it.tipo == TIPO_ALBUM && it.genero == generoFavorito }
                ?: albumesDelGenero.maxByOrNull { it.likes }
            )?.imagenUrl
        // La foto del artista: el artista del catalogo de esa banda o, si no esta, lo que me gusta de el
        val fotoDelArtista = (
            estado.items.firstOrNull { it.tipo == TIPO_ARTISTA && it.artista == artistaFavorito }
                ?: meGustan.firstOrNull { it.artista == artistaFavorito }
            )?.imagenUrl

        // El orden en que se descubren; las que no tienen datos no salen
        val tarjetas = listOfNotNull(
            TARJETA_RESENAS,
            TARJETA_GENERO.takeIf { generoFavorito != null },
            TARJETA_ARTISTA.takeIf { artistaFavorito != null },
            TARJETA_MEJOR.takeIf { mejor != null },
        )

        return estado.copy(
            anio = anio,
            totalResenas = misResenas.size,
            promedio = promedio(misResenas.map { it.first.rating }),
            totalMeGusta = estado.favoritos.size,
            generoFavorito = generoFavorito,
            portadaDelGenero = portadaDelGenero,
            artistaFavorito = artistaFavorito,
            fotoDelArtista = fotoDelArtista,
            mejorCalificado = mejor?.second?.toItemResumenUi(),
            mejorCalificacion = mejor?.first?.rating ?: 0,
            masQuerida = querida,
            pocosDatos = hayPocosDatos(resenas = misResenas.size, meGusta = estado.favoritos.size),
            tarjetas = tarjetas,
            // Si llegan datos nuevos no se pierde lo que ya se descubrio
            reveladas = estado.reveladas.coerceIn(1, tarjetas.size),
        )
    }

    // El texto que mas se repite; null si no hay ninguno
    private fun masRepetido(textos: List<String>): String? =
        textos.filter { it.isNotEmpty() }
            .groupBy { it }
            .maxByOrNull { (_, repetidos) -> repetidos.size }
            ?.key

    // Con un decimal: 4.333 queda 4.3. Sin reseñas, 0
    private fun promedio(calificaciones: List<Int>): Double {
        if (calificaciones.isEmpty()) return 0.0

        return (calificaciones.average() * 10).roundToInt() / 10.0
    }
}
