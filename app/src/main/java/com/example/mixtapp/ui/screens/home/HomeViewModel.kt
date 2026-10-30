package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.ui.screens.home.model.FILTRO_TENDENCIAS
import com.example.mixtapp.ui.screens.home.model.homeFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.mixtapp.ui.screens.home.model.FILTRO_ALFABETICO
import com.example.mixtapp.ui.screens.home.model.FILTRO_ANIO
import com.example.mixtapp.ui.screens.home.model.FILTRO_TOP_RATING
import kotlin.collections.sortedBy
import kotlin.collections.sortedByDescending

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val albumRepository: AlbumRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    // La pantalla la llama cada vez que se muestra: el init solo corre una vez al crear el
    // ViewModel, y al volver atras se seguirian viendo los datos viejos
    fun getAlbums() {
        // Al volver se conserva el filtro que estaba elegido
        val filtroInicial = _uiState.value.selectedFilterId.ifEmpty { homeFilters.first().id }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            // Cada album llega con sus resenas, y las mias ya vienen marcadas
            val result = albumRepository.getSongReviews()

            if (result.isSuccess) {
                val todos = result.getOrNull() ?: emptyList()

                _uiState.update {
                    it.copy(
                        albums = aplicarFiltro(filtroId = filtroInicial, todos = todos),
                        todos = todos,
                        trending = enTendencia(todos = todos),
                        friendActivity = actividadDeAmigos(todos = todos),
                        filters = homeFilters,
                        selectedFilterId = filtroInicial,
                        isLoading = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_cargar_contenido,
                        )
                    )
                }
            }
        }
    }

    // Tambien se llama al mostrarse: si cambie la foto en el perfil, al volver ya sale la nueva
    fun refrescarFotoDePerfil() {
        val foto = authRepository.currentUser?.photoUrl?.toString() ?: ""

        _uiState.update { it.copy(profileImageUrl = foto) }
    }

    // Los albumes ya estan cargados: cambiar de filtro no vuelve a pedirlos
    fun updateSelectedFilter(filtroId: String) {
        val todos = _uiState.value.todos

        _uiState.update {
            it.copy(
                selectedFilterId = filtroId,
                albums = aplicarFiltro(filtroId = filtroId, todos = todos),
            )
        }
    }

    private fun aplicarFiltro(filtroId: String, todos: List<SongReviewUi>): List<SongReviewUi> =
        when (filtroId) {
            FILTRO_TENDENCIAS -> todos.sortedByDescending { it.reviews.size }
            FILTRO_TOP_RATING -> todos.sortedByDescending { it.rating }
            FILTRO_ANIO -> todos.sortedByDescending { it.album.year.toIntOrNull() ?: 0 }
            FILTRO_ALFABETICO -> todos.sortedBy { it.album.title.lowercase() }
            else -> todos
        }

    // El album con mas resenas
    private fun enTendencia(todos: List<SongReviewUi>): SongReviewUi? =
        todos.sortedByDescending { it.reviews.size }.firstOrNull()

    // De las resenas de otros usuarios, la del album que se escucho mas recientemente
    private fun actividadDeAmigos(todos: List<SongReviewUi>): FriendActivityUi? {
        val actividades = mutableListOf<FriendActivityUi>()

        todos.forEach { songReview ->
            songReview.reviews.forEach { review ->
                if (!review.esMia) {
                    actividades.add(
                        FriendActivityUi(
                            id = review.id,
                            album = songReview.album,
                            rating = review.rating,
                            quote = review.content,
                            authorId = review.authorId,
                            author = review.author,
                            date = review.fecha,
                        )
                    )
                }
            }
        }

        // La fecha va como AAAA-MM-DD, asi que ordenar el texto ordena las fechas
        return actividades.sortedByDescending { it.date }.firstOrNull()
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}
