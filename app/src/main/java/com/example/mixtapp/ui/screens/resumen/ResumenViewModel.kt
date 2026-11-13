package com.example.mixtapp.ui.screens.resumen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.toItemResumenUi
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
import kotlin.math.roundToInt

// Desde esta calificacion mi resena cuenta como que el item me gusta, igual que en el inicio
private const val CALIFICACION_QUE_ME_GUSTA = 3

@HiltViewModel
class ResumenViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    private val usuarioRepository: UsuarioRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResumenState())
    val uiState: StateFlow<ResumenState> = _uiState.asStateFlow()

    // Los tres flujos son en vivo y no dependen de la ruta: se escuchan una sola vez al crear el
    // ViewModel. Si escribo una resena o doy un me gusta, el resumen se recalcula solo
    init {
        escucharCatalogo()
        escucharFavoritos()
        escucharPerfil()
    }

    // El catalogo trae cada item con sus reseñas, y las mias vienen marcadas
    private fun escucharCatalogo() {
        viewModelScope.launch(ioDispatcher) {
            catalogoRepository.getCatalogoLive()
                .catch { e ->
                    Log.d("ResumenViewModel", "escucharCatalogo: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { catalogo ->
                    _uiState.update {
                        armarResumen(estado = it.copy(catalogo = catalogo, isLoading = false, errorMessageRes = null))
                    }
                }
        }
    }

    private fun escucharFavoritos() {
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMisFavoritosLive()
                .catch { e -> Log.d("ResumenViewModel", "escucharFavoritos: ${e.message}") }
                .collect { favoritos ->
                    _uiState.update { armarResumen(estado = it.copy(favoritos = favoritos)) }
                }
        }
    }

    // Sin mi documento (cuenta nueva) los contadores quedan en 0: no es un error del resumen
    private fun escucharPerfil() {
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMiPerfilLive()
                .catch { e -> Log.d("ResumenViewModel", "escucharPerfil: ${e.message}") }
                .collect { perfil ->
                    _uiState.update { it.copy(seguidores = perfil.seguidores, seguidos = perfil.seguidos) }
                }
        }
    }

    // Descubre la siguiente tarjeta; al final se puede volver a empezar
    fun mostrarSiguiente() {
        _uiState.update { it.copy(reveladas = (it.reveladas + 1).coerceAtMost(it.tarjetas.size)) }
    }

    fun verDeNuevo() {
        _uiState.update { it.copy(reveladas = 1) }
    }

    // Se llama cada vez que cambia el catalogo o mis me gusta
    private fun armarResumen(estado: ResumenState): ResumenState {
        // Cada reseña mia con el item que califique
        val misResenas: List<Pair<ReviewUi, ItemUi>> = estado.catalogo.flatMap { conResenas ->
            conResenas.reviews.filter { it.isMine }.map { review -> review to conResenas.item }
        }
        val meGustan = loQueMeGusta(estado = estado, misResenas = misResenas)

        val mejor = misResenas.maxWithOrNull(compareBy({ it.first.rating }, { it.first.updatedAt }))
        val querida = misResenas.map { it.first }.filter { it.likes > 0 }.maxByOrNull { it.likes }
        val generoFavorito = masRepetido(meGustan.map { it.genero })
        val artistaFavorito = masRepetido(meGustan.map { it.artista })

        // El orden en que se descubren; las que no tienen datos no salen
        val tarjetas = listOfNotNull(
            TARJETA_RESENAS,
            TARJETA_GENERO.takeIf { generoFavorito != null },
            TARJETA_ARTISTA.takeIf { artistaFavorito != null },
            TARJETA_MEJOR.takeIf { mejor != null },
            TARJETA_QUERIDA.takeIf { querida != null },
            TARJETA_SOCIAL,
        )

        return estado.copy(
            totalResenas = misResenas.size,
            promedio = promedio(misResenas.map { it.first.rating }),
            totalMeGusta = estado.favoritos.size,
            generoFavorito = generoFavorito,
            artistaFavorito = artistaFavorito,
            mejorCalificado = mejor?.second?.toItemResumenUi(),
            mejorCalificacion = mejor?.first?.rating ?: 0,
            masQuerida = querida,
            tarjetas = tarjetas,
            // Si llegan datos nuevos no se pierde lo que ya se descubrio
            reveladas = estado.reveladas.coerceIn(1, tarjetas.size),
        )
    }

    // Lo que me gusta: lo que tiene mi me gusta y lo que reseñe con 3 estrellas o mas
    private fun loQueMeGusta(estado: ResumenState, misResenas: List<Pair<ReviewUi, ItemUi>>): List<ItemUi> {
        val idsConMiLike = estado.favoritos.map { it.id }.toSet()
        val conMiLike = estado.catalogo.map { it.item }.filter { it.id in idsConMiLike }
        val bienCalificados = misResenas.filter { it.first.rating >= CALIFICACION_QUE_ME_GUSTA }.map { it.second }

        return (conMiLike + bienCalificados).distinctBy { it.id }
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
