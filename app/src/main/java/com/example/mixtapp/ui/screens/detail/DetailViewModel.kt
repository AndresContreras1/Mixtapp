package com.example.mixtapp.ui.screens.detail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ReproductorRepository
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.SonandoRepository
import com.example.mixtapp.ui.utils.mensajeDeError
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
    private val sonandoRepository: SonandoRepository,
    private val reproductorRepository: ReproductorRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailState())
    val uiState: StateFlow<DetailState> = _uiState.asStateFlow()

    // El flujo se escucha mientras exista el ViewModel: si se volviera a pedir cada vez que la
    // pantalla se muestra, se acumularian listeners
    private var escuchando = false

    // La corrutina que escucha el fragmento que suena; null si no suena nada
    private var reproduccion: Job? = null

    // Datos en vivo: mi like, mi guardado y las resenas (una nueva, un like desde otro celular)
    // llegan solos y la pantalla se vuelve a pintar, sin salir y volver a entrar
    fun getItem(tipo: String, itemId: String) {
        if (escuchando) return
        escuchando = true

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            catalogoRepository.getItemLive(tipo = tipo, itemId = itemId)
                .catch { e ->
                    Log.d("DetailViewModel", "getItem: ${e.message}")
                    // Si se cerro con error se puede volver a escuchar al entrar de nuevo
                    escuchando = false
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(
                                error = e,
                                generico = R.string.error_cargar_contenido,
                                noEncontrado = R.string.item_no_encontrado,
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

    // No se copia el estado a mano: la transaccion cambia likesCount del item, eso dispara el
    // listener y llega el corazon y el contador al dia
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
                            noEncontrado = R.string.item_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    // Pone el item en "sonando ahora". Mis seguidores lo ven en Siguiendo por el listener; aqui
    // solo queda el aviso de que ya se puso
    fun ponerSonando() {
        val item = _uiState.value.itemConResenas?.item ?: return

        viewModelScope.launch(ioDispatcher) {
            val result = sonandoRepository.ponerSonando(item = item)

            if (result.isSuccess) {
                _uiState.update { it.copy(avisoSonandoRes = R.string.sonando_publicado, errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(
                        avisoSonandoRes = null,
                        errorAccionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_sonando,
                        )
                    )
                }
            }
        }
    }

    // Guarda el item en mi perfil o lo quita. El marcador cambia cuando llega mi guardado por el
    // listener, y el item aparece en la pestana Guardados del perfil
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
                            noEncontrado = R.string.item_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    // El like de una resena: tampoco se copia a mano, llega la resena nueva por el listener del
    // detalle, en este celular y en los demas que tengan el item abierto
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
                            noEncontrado = R.string.item_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    // Al quitar mi resena no se toca la lista a mano: el listener trae las resenas sin ella y el
    // promedio recalculado
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
                            noEncontrado = R.string.item_no_encontrado,
                        )
                    )
                }
            }
        }
    }

    // El play del detalle: el fragmento del item que se esta viendo
    fun alternarVistaPreviaDelItem() {
        val item = _uiState.value.itemConResenas?.item ?: return
        alternarVistaPrevia(item = item)
    }

    // Tocar play en lo que ya suena lo detiene; en otro item, detiene lo de antes y pide su fragmento.
    // El audio suena mientras se escuche el flujo: cancelar la corrutina lo detiene y lo libera
    fun alternarVistaPrevia(item: ItemUi) {
        val eraEste = _uiState.value.vistaPreviaId == item.id
        detenerVistaPrevia()
        if (eraEste) return

        reproduccion = viewModelScope.launch(ioDispatcher) {
            _uiState.update {
                it.copy(
                    vistaPreviaId = item.id,
                    vistaPreviaEstado = EstadoDeReproduccion.Cargando,
                    errorVistaPreviaRes = null,
                )
            }

            val result = reproductorRepository.getVistaPrevia(item = item)
            val url = result.getOrNull()
            if (url == null) {
                _uiState.update {
                    it.copy(
                        vistaPreviaId = null,
                        vistaPreviaEstado = null,
                        errorVistaPreviaRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_vista_previa,
                        ),
                    )
                }
                return@launch
            }

            reproductorRepository.escuchar(url = url)
                .catch { e ->
                    Log.d("DetailViewModel", "alternarVistaPrevia: ${e.message}")
                    _uiState.update {
                        it.copy(
                            vistaPreviaId = null,
                            vistaPreviaEstado = null,
                            errorVistaPreviaRes = mensajeDeError(error = e, generico = R.string.error_vista_previa),
                        )
                    }
                }
                .collect { estado ->
                    // Al terminar los 30 segundos el boton vuelve al play
                    _uiState.update {
                        if (estado == EstadoDeReproduccion.Terminada) {
                            it.copy(vistaPreviaId = null, vistaPreviaEstado = null)
                        } else {
                            it.copy(vistaPreviaEstado = estado)
                        }
                    }
                }
        }
    }

    // Al salir de la pantalla o al tocar otro play: la musica no sigue sonando donde no se ve
    fun detenerVistaPrevia() {
        reproduccion?.cancel()
        reproduccion = null
        _uiState.update { it.copy(vistaPreviaId = null, vistaPreviaEstado = null) }
    }
}
