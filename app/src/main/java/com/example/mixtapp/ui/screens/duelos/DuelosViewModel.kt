package com.example.mixtapp.ui.screens.duelos

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.DueloUi
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.DueloRepository
import com.example.mixtapp.ui.screens.duelos.model.dueloTabs
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

private const val MILIS_POR_DIA = 24 * 60 * 60 * 1000L
// Colombia va 5 horas detras de la hora universal: el duelo cambia a la medianoche de Colombia
private const val DESFASE_COLOMBIA = 5 * 60 * 60 * 1000L
// Lo mas largo que puede ser una opinion
const val MAX_OPINION = 140

@HiltViewModel
class DuelosViewModel @Inject constructor(
    private val catalogoRepository: CatalogoRepository,
    private val dueloRepository: DueloRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(DuelosState())
    val uiState: StateFlow<DuelosState> = _uiState.asStateFlow()

    // Todos los celulares calculan el mismo numero de dia: con el sale el mismo duelo y el mismo id
    private val dia: Int = ((System.currentTimeMillis() - DESFASE_COLOMBIA) / MILIS_POR_DIA).toInt()

    // Los cuatro flujos son en vivo y los ids de hoy no dependen de la ruta: se escuchan una sola
    // vez al crear el ViewModel. Cada uno va en su propio launch porque collect no termina
    init {
        escucharCatalogo()
        dueloTabs.forEach { tab -> escucharDuelo(tipo = tab.tipo) }
    }

    // Con el catalogo se arma el par de hoy de cada tipo, aunque nadie haya votado todavia
    private fun escucharCatalogo() {
        viewModelScope.launch(ioDispatcher) {
            catalogoRepository.getCatalogoLive()
                .catch { e ->
                    Log.d("DuelosViewModel", "escucharCatalogo: ${e.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                        )
                    }
                }
                .collect { catalogo ->
                    _uiState.update {
                        armarDuelos(estado = it.copy(catalogo = catalogo, isLoading = false, errorMessageRes = null))
                    }
                }
        }
    }

    // Cada voto de cualquier usuario cambia los porcentajes y las opiniones sin recargar
    private fun escucharDuelo(tipo: String) {
        viewModelScope.launch(ioDispatcher) {
            dueloRepository.getDueloLive(dueloId = dueloId(tipo = tipo), tipo = tipo)
                .catch { e ->
                    Log.d("DuelosViewModel", "escucharDuelo: ${e.message}")
                    _uiState.update {
                        it.copy(errorVotosRes = mensajeDeError(error = e, generico = R.string.error_cargar_votos))
                    }
                }
                .collect { enVivo ->
                    _uiState.update {
                        armarDuelos(estado = it.copy(enVivo = it.enVivo + (tipo to enVivo), errorVotosRes = null))
                    }
                }
        }
    }

    fun selectTipo(tipo: String) {
        _uiState.update {
            it.copy(tipoElegido = tipo, opcionElegida = null, opinion = "", errorVotoRes = null)
        }
    }

    // Tocar uno de los dos lo deja elegido; el voto se guarda con el boton
    fun elegirOpcion(opcion: String) {
        _uiState.update { it.copy(opcionElegida = opcion, errorVotoRes = null) }
    }

    fun updateOpinion(texto: String) {
        _uiState.update { it.copy(opinion = texto.take(MAX_OPINION)) }
    }

    // Se valida al presionar Votar. Los porcentajes y mi opinion cambian cuando llega el voto por el
    // listener: aqui no se suma nada a mano
    fun votar() {
        val estado = _uiState.value
        val duelo = estado.duelos[estado.tipoElegido] ?: return
        val opcion = estado.opcionElegida ?: duelo.miOpcion

        if (opcion == null) {
            _uiState.update { it.copy(errorVotoRes = R.string.error_elige_uno) }
            return
        }

        _uiState.update { it.copy(enviando = true, errorVotoRes = null) }

        viewModelScope.launch(ioDispatcher) {
            val result = dueloRepository.votar(duelo = duelo, opcion = opcion, opinion = estado.opinion.trim())

            if (result.isSuccess) {
                _uiState.update { it.copy(enviando = false, opcionElegida = null, opinion = "") }
            } else {
                _uiState.update {
                    it.copy(
                        enviando = false,
                        errorVotoRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_votar),
                    )
                }
            }
        }
    }

    // El mismo id en todos los celulares: el numero del dia y el tipo
    private fun dueloId(tipo: String): String = "dia_${dia}_$tipo"

    // Se llama cada vez que cambia el catalogo o los votos de un duelo
    private fun armarDuelos(estado: DuelosState): DuelosState {
        val duelos = dueloTabs.mapNotNull { tab ->
            armarDuelo(tipo = tab.tipo, estado = estado)?.let { duelo -> tab.tipo to duelo }
        }.toMap()

        return estado.copy(duelos = duelos)
    }

    // Si alguien ya voto, los dos items salen del documento del duelo: asi no cambian aunque cambie el
    // catalogo. Si no, salen del par de hoy
    private fun armarDuelo(tipo: String, estado: DuelosState): DueloUi? {
        val enVivo = estado.enVivo[tipo]
        val itemA = enVivo?.itemA
        val itemB = enVivo?.itemB
        val genero = enVivo?.genero
        val par = if (itemA != null && itemB != null && genero != null) {
            ParDelDia(genero = genero, itemA = itemA, itemB = itemB)
        } else {
            parDelDia(catalogo = estado.catalogo, tipo = tipo)
        } ?: return null

        return DueloUi(
            id = dueloId(tipo = tipo),
            dia = dia,
            tipo = tipo,
            genero = par.genero,
            itemA = par.itemA,
            itemB = par.itemB,
            votosA = enVivo?.votosA ?: 0,
            votosB = enVivo?.votosB ?: 0,
            opiniones = enVivo?.opiniones ?: emptyList(),
            miOpcion = enVivo?.miOpcion,
        )
    }

    // El par de hoy: cada tipo toma un genero distinto que va rotando con los dias, y dentro del
    // genero dos items distintos que tambien rotan. Solo cuentan los generos con dos items o mas.
    // Se ordena por id para que todos los celulares lleguen al mismo par
    private fun parDelDia(catalogo: List<ItemConResenasUi>, tipo: String): ParDelDia? {
        val delTipo = catalogo.map { it.item }.filter { it.tipo == tipo }
        val porGenero = delTipo.groupBy { it.genero }.filter { (_, items) -> items.size >= 2 }
        val generos = porGenero.keys.sorted()
        if (generos.isEmpty()) return null

        val posicionDelTipo = dueloTabs.indexOfFirst { it.tipo == tipo }
        val genero = generos[(dia + posicionDelTipo) % generos.size]
        val candidatos = porGenero[genero].orEmpty().sortedBy { it.id }
        val cuantos = candidatos.size
        val primero = dia % cuantos
        // Nunca el mismo: se salta entre 1 y cuantos - 1 posiciones
        val segundo = (primero + 1 + (dia / cuantos) % (cuantos - 1)) % cuantos

        return ParDelDia(
            genero = genero,
            itemA = candidatos[primero].toItemResumenUi(),
            itemB = candidatos[segundo].toItemResumenUi(),
        )
    }
}

// Los dos items que se enfrentan hoy y su genero
private data class ParDelDia(
    val genero: String,
    val itemA: ItemResumenUi,
    val itemB: ItemResumenUi,
)
