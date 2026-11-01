package com.example.mixtapp.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.model.Album
import com.example.mixtapp.data.model.FAVORITOS_EN_PERFIL
import com.example.mixtapp.data.model.MAX_REVIEW_LENGTH
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.StorageRepository
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.ui.screens.profile.model.profileTabs
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

// La calificacion va de 0 a 5; un album con 5 cuenta como favorito
private const val CALIFICACION_MAXIMA = 5

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val storageRepository: StorageRepository,
    private val reviewRepository: ReviewRepository,
    private val usuarioRepository: UsuarioRepository,
    // Las corrutinas se lanzan en este hilo; las pruebas pasan uno de prueba
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    // Los flujos se escuchan mientras exista el ViewModel: si se volvieran a pedir cada vez que la
    // pantalla se muestra, se acumularian listeners
    private var escuchandoPerfil = false
    private var escuchandoResenas = false

    // La pantalla la llama cada vez que se muestra: la foto se lee de nuevo y mis datos y mis
    // resenas llegan en vivo, asi que un seguidor o un like desde otro celular se ven sin salir
    fun getProfile() {
        val usuarioDelCorreo = authRepository.usuarioDelCorreo
        val foto = authRepository.currentUser?.photoUrl?.toString() ?: ""
        val usuarioId = authRepository.currentUser?.uid ?: ""

        _uiState.update { it.copy(usuarioId = usuarioId, profileImageUrl = foto) }

        // Mis datos y mis resenas se usan por su lado, asi que cada uno va en su propio launch
        if (!escuchandoPerfil) {
            escuchandoPerfil = true

            viewModelScope.launch(ioDispatcher) {
                usuarioRepository.getMiPerfilLive()
                    .catch { e ->
                        Log.d("ProfileViewModel", "getProfile: ${e.message}")
                        // Al volver a la pantalla se intenta de nuevo: asi sale el perfil recien completado
                        escuchandoPerfil = false
                        _uiState.update {
                            it.copy(
                                // Si mis datos ya estaban cargados se conservan; si no, sale la parte del correo
                                usuario = it.usuario.ifEmpty { usuarioDelCorreo },
                                // Sin mi documento en Firestore se pide completar el perfil; otro error se avisa
                                avisoPerfilRes = if (e is ContenidoNoEncontradoException) {
                                    R.string.completa_tu_perfil
                                } else {
                                    mensajeDeError(error = e, generico = R.string.error_cargar_contenido)
                                },
                            )
                        }
                    }
                    .collect { miPerfil ->
                        // Los contadores vienen del documento: no hace falta leer las listas
                        _uiState.update {
                            it.copy(
                                usuario = miPerfil.username,
                                nombre = miPerfil.nombre,
                                bio = miPerfil.bio,
                                seguidores = miPerfil.seguidores,
                                seguidos = miPerfil.seguidos,
                                avisoPerfilRes = null,
                            )
                        }
                    }
            }
        }

        if (!escuchandoResenas) {
            escuchandoResenas = true

            viewModelScope.launch(ioDispatcher) {
                _uiState.update { it.copy(isLoading = true, errorMessageRes = null) }

                reviewRepository.getMyReviewsLive()
                    .catch { e ->
                        Log.d("ProfileViewModel", "getProfile: ${e.message}")
                        escuchandoResenas = false
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                tabs = profileTabs,
                                // Al volver se conserva la pestana elegida
                                selectedTabId = it.selectedTabId.ifEmpty { profileTabs.first().id },
                                errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                            )
                        }
                    }
                    .collect { resenas ->
                        // La fecha va como AAAA-MM-DD: la escuchada mas reciente queda arriba
                        val misResenas = resenas.sortedByDescending { it.date }

                        _uiState.update {
                            it.copy(
                                misResenas = misResenas,
                                favoritos = favoritosDe(resenas = misResenas),
                                barrasDeCalificacion = barrasDe(resenas = misResenas),
                                isLoading = false,
                                tabs = profileTabs,
                                // Al volver se conserva la pestana elegida
                                selectedTabId = it.selectedTabId.ifEmpty { profileTabs.first().id },
                                // Si estaba editando una resena que sigue existiendo, la edicion no se pierde
                                resenaEnEdicionId = if (misResenas.any { r -> r.id == it.resenaEnEdicionId }) {
                                    it.resenaEnEdicionId
                                } else {
                                    null
                                },
                                errorMessageRes = null,
                            )
                        }
                    }
            }
        }
    }

    // Eliminar filtra la lista en vez de volver a pedir todas las resenas
    fun eliminarResena(reviewId: String) {
        if (_uiState.value.eliminando) return

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(eliminando = true, errorMessageRes = null) }

            val result = reviewRepository.eliminarResena(reviewId = reviewId)

            if (result.isSuccess) {
                val restantes = _uiState.value.misResenas.filter { it.id != reviewId }

                _uiState.update {
                    it.copy(
                        misResenas = restantes,
                        favoritos = favoritosDe(resenas = restantes),
                        barrasDeCalificacion = barrasDe(resenas = restantes),
                        eliminando = false,
                        errorMessageRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        eliminando = false,
                        errorMessageRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_eliminar_resena,
                        )
                    )
                }
            }
        }
    }

    // La resena se edita en la misma tarjeta del perfil, sin ir a otra pantalla
    fun empezarEdicion(resena: MyReviewUi) {
        _uiState.update {
            it.copy(
                resenaEnEdicionId = resena.id,
                calificacionEditada = resena.rating,
                textoEditado = resena.excerpt,
                errorEdicionRes = null,
            )
        }
    }

    fun updateCalificacionEditada(rating: Int) {
        val calificacionActual = _uiState.value.calificacionEditada
        val nueva = if (rating == calificacionActual) 0 else rating

        _uiState.update { it.copy(calificacionEditada = nueva) }
    }

    fun updateTextoEditado(texto: String) {
        _uiState.update {
            it.copy(textoEditado = texto.take(MAX_REVIEW_LENGTH), errorEdicionRes = null)
        }
    }

    fun cancelarEdicion() {
        _uiState.update { it.copy(resenaEnEdicionId = null, errorEdicionRes = null) }
    }

    fun guardarEdicion() {
        val estado = _uiState.value
        val resena = estado.misResenas.find { it.id == estado.resenaEnEdicionId } ?: return
        val texto = estado.textoEditado.trim()

        if (estado.guardandoEdicion) return

        if (texto.isEmpty()) {
            _uiState.update { it.copy(errorEdicionRes = R.string.error_resena_vacia) }
            return
        }

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(guardandoEdicion = true, errorEdicionRes = null) }

            val result = reviewRepository.guardarResena(
                albumId = resena.album.id,
                rating = estado.calificacionEditada,
                texto = texto,
                fecha = resena.date,
                reviewId = resena.id,
            )

            if (result.isSuccess) {
                // Solo cambia esa resena en la lista, sin volver a pedirlas todas
                val actualizadas = _uiState.value.misResenas.map { miResena ->
                    if (miResena.id == resena.id) {
                        miResena.copy(rating = estado.calificacionEditada, excerpt = texto)
                    } else {
                        miResena
                    }
                }

                _uiState.update {
                    it.copy(
                        misResenas = actualizadas,
                        favoritos = favoritosDe(resenas = actualizadas),
                        barrasDeCalificacion = barrasDe(resenas = actualizadas),
                        resenaEnEdicionId = null,
                        guardandoEdicion = false,
                        errorEdicionRes = null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        guardandoEdicion = false,
                        errorEdicionRes = mensajeDeError(
                            error = result.exceptionOrNull(),
                            generico = R.string.error_publicar_resena,
                        ),
                    )
                }
            }
        }
    }

    fun subirFotoDePerfil(uri: Uri) {
        Log.d("ProfileViewModel", uri.toString())

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(subiendoImagen = true, errorImagenRes = null) }

            val result = storageRepository.uploadProfileImage(uri = uri)

            if (result.isSuccess) {
                val url = result.getOrNull() ?: ""

                // El documento de Firestore tambien guarda la foto para que otros la vean en mi perfil
                val guardada = usuarioRepository.actualizarFotoDePerfil(fotoUrl = url)

                _uiState.update {
                    it.copy(
                        subiendoImagen = false,
                        profileImageUrl = url,
                        // La foto ya quedo en Storage; si el documento no se actualiza, se avisa
                        errorImagenRes = if (guardada.isSuccess) null else R.string.error_guardar_perfil,
                    )
                }
            } else {
                Log.d("ProfileViewModel", result.exceptionOrNull()?.message.toString())

                _uiState.update {
                    it.copy(
                        subiendoImagen = false,
                        errorImagenRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_subir_imagen),
                    )
                }
            }
        }
    }

    // Los favoritos son los albumes que califique con 5
    private fun favoritosDe(resenas: List<MyReviewUi>): List<Album> =
        resenas.filter { it.rating == CALIFICACION_MAXIMA }.map { it.album }.take(FAVORITOS_EN_PERFIL)

    // Cuantas resenas tengo con cada calificacion, de 0 a 5, como el alto de cada barra
    private fun barrasDe(resenas: List<MyReviewUi>): List<Float> {
        val cantidades = (0..CALIFICACION_MAXIMA).map { estrellas ->
            resenas.count { it.rating == estrellas }
        }
        val mayor = cantidades.maxOrNull() ?: 0

        return cantidades.map { cantidad ->
            if (mayor == 0) 0f else cantidad.toFloat() / mayor
        }
    }



    fun updateSelectedTab(tabId: String) {
        _uiState.update { it.copy(selectedTabId = tabId) }
    }

    // signOut no lleva suspend: solo borra la sesion del celular, no va a la red
    fun cerrarSesion() {
        authRepository.signOut()
    }
}
