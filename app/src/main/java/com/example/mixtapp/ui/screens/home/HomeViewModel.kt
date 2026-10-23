package com.example.mixtapp.ui.screens.home

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.data.repository.AlbumRepository
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.home.model.FILTRO_SEGUIDOS
import com.example.mixtapp.ui.screens.home.model.FILTRO_TENDENCIAS
import com.example.mixtapp.ui.screens.home.model.homeFilters
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val albumRepository: AlbumRepository,
    private val usuarioRepository: UsuarioRepository
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
            val result = albumRepository.getAlbumReviews()
            val followedResult = usuarioRepository.getMisSeguidos()

            if (result.isSuccess && followedResult.isSuccess) {
                val todos = result.getOrNull() ?: emptyList()
                val followedIds = (followedResult.getOrNull() ?: emptyList()).map { it.id }.toSet()

                _uiState.update {
                    it.copy(
                        followedIds = followedIds,
                        albums = aplicarFiltro(filtroId = filtroInicial, todos = todos, followedIds = followedIds),
                        todos = todos,
                        trending = enTendencia(todos = todos),
                        friendActivity = actividadDeSeguidos(todos = todos, followedIds = followedIds),
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
                            error = result.exceptionOrNull() ?: followedResult.exceptionOrNull(),
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
        val state = _uiState.value
        val todos = state.todos
        val followedIds = state.followedIds

        _uiState.update {
            it.copy(
                selectedFilterId = filtroId,
                albums = aplicarFiltro(filtroId = filtroId, todos = todos, followedIds = followedIds),
            )
        }
    }

    private fun aplicarFiltro(filtroId: String, todos: List<AlbumReviewsUi>, followedIds: Set<String>): List<AlbumReviewsUi> =
        when (filtroId) {
            FILTRO_TENDENCIAS -> todos.sortedByDescending { it.reviews.size }

            // Los albumes que reseno alguien a quien sigo
            FILTRO_SEGUIDOS -> todos.filter { albumReviews ->
                albumReviews.reviews.any { !it.esMia && it.authorId in followedIds }
            }

            else -> todos
        }

    // El album con mas resenas
    private fun enTendencia(todos: List<AlbumReviewsUi>): AlbumReviewsUi? =
        todos.sortedByDescending { it.reviews.size }.firstOrNull()

    // De las resenas de usuarios seguidos, la del album que se escucho mas recientemente
    private fun actividadDeSeguidos(todos: List<AlbumReviewsUi>, followedIds: Set<String>): FriendActivityUi? {
        val actividades = mutableListOf<FriendActivityUi>()

        todos.forEach { albumReviews ->
            albumReviews.reviews.forEach { review ->
                if (!review.esMia && review.authorId in followedIds) {
                    actividades.add(
                        FriendActivityUi(
                            id = review.id,
                            album = albumReviews.album,
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
