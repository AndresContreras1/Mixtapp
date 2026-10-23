package com.example.mixtapp.ui.screens.home

import android.util.Log
import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.data.model.FriendActivityUi
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
import kotlinx.coroutines.flow.catch
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

    // Los datos son en vivo: el flujo se escucha mientras exista el ViewModel, asi que se empieza
    // una sola vez aqui. Si se pidiera en cada LaunchedEffect se acumularian listeners
    init {
        escucharAlbumes()
    }

    // Cada vez que alguien escribe, edita, borra o le da like a una resena, llegan los albumes de
    // nuevo y la pantalla se vuelve a pintar sola
    private fun escucharAlbumes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

            albumRepository.getAlbumReviewsLive()
                .catch { e ->
                    Log.d("HomeViewModel", "escucharAlbumes: ${e.message}")
                    _uiState.update { it.copy(isLoading = false, errorMessageRes = R.string.error_cargar_contenido) }
                }
                .collect { todos ->
                    _uiState.update {
                        // Al llegar datos nuevos se conserva el filtro que estaba elegido
                        val filtroId = it.selectedFilterId.ifEmpty { homeFilters.first().id }
                        val resenasDeSeguidos = resenasDeSeguidos(todos = todos, followedIds = it.followedIds)

                        it.copy(
                            albums = aplicarFiltro(filtroId = filtroId, todos = todos),
                            todos = todos,
                            trending = enTendencia(todos = todos),
                            resenasDeSeguidos = resenasDeSeguidos,
                            filters = homeFilters,
                            selectedFilterId = filtroId,
                            soloSeguidos = filtroId == FILTRO_SEGUIDOS,
                            isLoading = false,
                            errorMessageRes = null,
                        )
                    }
                }
        }
    }

    // La pantalla la llama cada vez que se muestra: si segui o deje de seguir a alguien, al volver
    // ya cuenta en el filtro de seguidos
    fun cargarSeguidos() {
        viewModelScope.launch {
            val result = usuarioRepository.getMisSeguidos()

            if (result.isSuccess) {
                val followedIds = (result.getOrNull() ?: emptyList()).map { it.id }.toSet()

                _uiState.update {
                    it.copy(
                        followedIds = followedIds,
                        resenasDeSeguidos = resenasDeSeguidos(todos = it.todos, followedIds = followedIds),
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
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
        _uiState.update {
            it.copy(
                selectedFilterId = filtroId,
                albums = aplicarFiltro(filtroId = filtroId, todos = it.todos),
                soloSeguidos = filtroId == FILTRO_SEGUIDOS,
            )
        }
    }

    private fun aplicarFiltro(filtroId: String, todos: List<AlbumReviewsUi>): List<AlbumReviewsUi> =
        when (filtroId) {
            FILTRO_TENDENCIAS -> todos.sortedByDescending { it.reviews.size }
            else -> todos
        }

    // El album con mas resenas
    private fun enTendencia(todos: List<AlbumReviewsUi>): AlbumReviewsUi? =
        todos.sortedByDescending { it.reviews.size }.firstOrNull()

    // Las resenas que escribieron los usuarios que sigo, de la escuchada mas reciente a la mas antigua
    private fun resenasDeSeguidos(todos: List<AlbumReviewsUi>, followedIds: Set<String>): List<FriendActivityUi> {
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
        return actividades.sortedByDescending { it.date }
    }

    @StringRes
    private fun mensajeDeError(error: Throwable?, @StringRes generico: Int): Int = when (error) {
        is ContenidoNoEncontradoException -> R.string.contenido_no_encontrado
        is ErrorDelServidorException -> R.string.error_servidor
        is SinConexionException -> R.string.error_sin_conexion
        else -> generico
    }
}
