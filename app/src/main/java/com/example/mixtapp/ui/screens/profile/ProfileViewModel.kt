package com.example.mixtapp.ui.screens.profile

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mixtapp.R
import com.example.mixtapp.data.injection.IoDispatcher
import com.example.mixtapp.data.repository.AuthRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ReviewRepository
import com.example.mixtapp.data.repository.StorageRepository
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

// Cuantas de mis resenas salen en Actividad reciente; las demas estan en Mis resenas
private const val RESENAS_RECIENTES = 3

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

    // Mis datos se pueden volver a pedir al volver a la pantalla: la bandera evita un segundo listener
    private var escuchandoPerfil = false

    // Ningun flujo de mi perfil necesita datos de la ruta, asi que todos se escuchan aqui una sola
    // vez, mientras exista el ViewModel. Cada uno va en su propio launch porque collect no termina
    init {
        // Mi uid abre mis listas de seguidores y seguidos aunque mi documento no exista. La foto de
        // la cuenta se muestra mientras llega la de mi documento
        _uiState.update {
            it.copy(
                miId = authRepository.currentUser?.uid ?: "",
                profileImageUrl = authRepository.currentUser?.photoUrl?.toString() ?: "",
            )
        }

        escucharMiPerfil()
        escucharMisResenas()
        escucharMisFavoritos()
        escucharMisGuardados()
    }

    // Mis datos en vivo: si alguien me sigue desde otro celular, el contador cambia solo. Si mi
    // documento no existia, el flujo termina; la pantalla lo pide otra vez al volver de Ajustes y
    // ya sale el perfil completo
    fun escucharMiPerfil() {
        if (escuchandoPerfil) return
        escuchandoPerfil = true

        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMiPerfilLive()
                .catch { e ->
                    Log.d("ProfileViewModel", "escucharMiPerfil: ${e.message}")
                    escuchandoPerfil = false

                    if (e is ContenidoNoEncontradoException) {
                        // Sin documento no es un error: sale el usuario del correo y el aviso de completarlo
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                usuario = it.usuario.ifEmpty { authRepository.usuarioDelCorreo },
                                avisoPerfilRes = R.string.completa_tu_perfil,
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessageRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido),
                            )
                        }
                    }
                }
                .collect { miPerfil ->
                    // Los contadores vienen del documento: no hace falta contar las listas
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            usuario = miPerfil.username,
                            nombre = miPerfil.nombre,
                            bio = miPerfil.bio,
                            instagram = miPerfil.instagram,
                            instagramUrl = miPerfil.instagramUrl,
                            seguidores = miPerfil.seguidores,
                            seguidos = miPerfil.seguidos,
                            // La del documento es la que ven los demas; sin ella queda la de la cuenta
                            profileImageUrl = miPerfil.fotoUrl.ifEmpty { it.profileImageUrl },
                            avisoPerfilRes = null,
                            errorMessageRes = null,
                        )
                    }
                }
        }
    }

    // Mis resenas en vivo, la ultima publicada o editada primero: al editar o borrar una, la
    // actividad cambia sin volver a pedirla
    private fun escucharMisResenas() {
        viewModelScope.launch(ioDispatcher) {
            reviewRepository.getMyReviewsLive()
                .catch { e ->
                    Log.d("ProfileViewModel", "escucharMisResenas: ${e.message}")
                    _uiState.update { it.copy(errorResenasRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido)) }
                }
                .collect { resenas ->
                    _uiState.update { it.copy(resenasRecientes = resenas.take(RESENAS_RECIENTES), errorResenasRes = null) }
                }
        }
    }

    // Mis favoritos son mis likes: albumes, canciones y artistas mezclados, el mas nuevo primero
    private fun escucharMisFavoritos() {
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMisFavoritosLive()
                .catch { e ->
                    Log.d("ProfileViewModel", "escucharMisFavoritos: ${e.message}")
                    _uiState.update { it.copy(errorFavoritosRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido)) }
                }
                .collect { favoritos ->
                    _uiState.update { it.copy(favoritos = favoritos, errorFavoritosRes = null) }
                }
        }
    }

    // Lo que guarde con el boton de guardar del detalle
    private fun escucharMisGuardados() {
        viewModelScope.launch(ioDispatcher) {
            usuarioRepository.getMisGuardadosLive()
                .catch { e ->
                    Log.d("ProfileViewModel", "escucharMisGuardados: ${e.message}")
                    _uiState.update { it.copy(errorGuardadosRes = mensajeDeError(error = e, generico = R.string.error_cargar_contenido)) }
                }
                .collect { guardados ->
                    _uiState.update { it.copy(guardados = guardados, errorGuardadosRes = null) }
                }
        }
    }

    // El corazon y el contador no se cambian a mano: llegan con la resena nueva por el listener
    fun sendOrDeleteLike(reviewId: String) {
        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.sendOrDeleteLike(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(errorAccionRes = null) }
            } else {
                _uiState.update {
                    it.copy(errorAccionRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_actualizar_like))
                }
            }
        }
    }

    // La resena no se quita de la lista a mano: el listener de mis resenas trae la lista sin ella
    fun eliminarResena(reviewId: String) {
        // Se marca antes de lanzar la corrutina: asi un doble toque no manda dos borrados
        if (_uiState.value.eliminando) return
        _uiState.update { it.copy(eliminando = true, errorAccionRes = null) }

        viewModelScope.launch(ioDispatcher) {
            val result = reviewRepository.eliminarResena(reviewId = reviewId)

            if (result.isSuccess) {
                _uiState.update { it.copy(eliminando = false) }
            } else {
                _uiState.update {
                    it.copy(
                        eliminando = false,
                        errorAccionRes = mensajeDeError(error = result.exceptionOrNull(), generico = R.string.error_eliminar_resena),
                    )
                }
            }
        }
    }

    // La foto se sube a Storage y despues se copia en mi documento, para que otros la vean
    fun subirFotoDePerfil(uri: Uri) {
        Log.d("ProfileViewModel", uri.toString())

        viewModelScope.launch(ioDispatcher) {
            _uiState.update { it.copy(subiendoImagen = true, errorImagenRes = null) }

            val result = storageRepository.uploadProfileImage(uri = uri)

            if (result.isSuccess) {
                val url = result.getOrNull() ?: ""

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

    // La pestana elegida vive en el estado: al volver de otra pantalla sigue la misma
    fun updateSelectedTab(tabId: String) {
        _uiState.update { it.copy(selectedTabId = tabId) }
    }

    // signOut no lleva suspend: solo borra la sesion del celular, no va a la red
    fun cerrarSesion() {
        authRepository.signOut()
    }
}
