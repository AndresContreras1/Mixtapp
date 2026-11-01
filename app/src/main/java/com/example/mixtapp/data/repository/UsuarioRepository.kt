package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.MessagingRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.ActualizarUsuarioDto
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.toItemResumenUi
import com.example.mixtapp.data.dtos.toReviewUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.UsuarioUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject

// Recibe el contrato: Hilt entrega la implementacion de Firestore desde DataSourceModule. Para
// volver al backend de Express basta con cambiar ese @Binds a UsuarioRetrofitDataSourceImpl.
// El backend todavia no tiene rutas para guardar usuarios ni seguidores, asi que esas funciones
// quedan como to-do en esa implementacion, como en clase
class UsuarioRepository @Inject constructor(
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val messagingRemoteDataSource: MessagingRemoteDataSource
) {

    // El perfil en vivo: si alguien lo sigue desde otro celular, el contador cambia sin salir.
    // Con mi uid el data source dice si ya lo sigo
    fun getUsuarioByIdLive(id: String): Flow<UsuarioUi> {
        val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

        return usuarioRemoteDataSource.listenUsuarioById(id, currentUserId = usuarioActualId)
            .map { dto ->
                // El data source envia null si no existe: el repositorio decide el error
                val usuario = dto?.toUsuarioUi() ?: throw ContenidoNoEncontradoException()
                usuario.copy(esMiPerfil = usuario.id == usuarioActualId)
            }
            .catch { e ->
                Log.d("UsuarioRepository", "getUsuarioByIdLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Sigue o deja de seguir: el data source decide segun si ya lo sigo. El boton y el contador
    // cambian cuando llega el documento nuevo por el listener
    suspend fun followOrUnfollow(targetUserId: String): Result<Unit> {
        return try {
            val usuarioActualId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            // Nadie se sigue a si mismo
            if (targetUserId == usuarioActualId) return Result.failure(SeguirseASiMismoException())

            // La transaccion cambia el contador de los dos documentos: si el otro no existe, el
            // repositorio decide el error, y si falta el mio se crea antes de seguir
            if (usuarioRemoteDataSource.getUsuarioById(targetUserId) == null) {
                return Result.failure(ContenidoNoEncontradoException())
            }
            crearMiDocumentoSiFalta(usuarioId = usuarioActualId)

            usuarioRemoteDataSource.followOrUnfollow(currentUserId = usuarioActualId, targetUserId = targetUserId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "followOrUnfollow: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "followOrUnfollow: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "followOrUnfollow: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Mis contadores en vivo: si alguien me sigue desde otro celular, el numero cambia solo
    fun getMiPerfilLive(): Flow<UsuarioUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return usuarioRemoteDataSource.listenUsuarioById(usuarioId)
            .map { dto ->
                // Sin documento el perfil avisa que hay que completarlo en Editar perfil
                val usuario = dto?.toUsuarioUi() ?: throw ContenidoNoEncontradoException()
                usuario.copy(esMiPerfil = true)
            }
            .catch { e ->
                Log.d("UsuarioRepository", "getMiPerfilLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Mi perfil es el documento que tiene el uid de Authentication como id
    suspend fun getMiPerfil(): Result<UsuarioUi> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            // El data source retorna null si no existe: el repositorio es el que decide el error
            val usuario = usuarioRemoteDataSource.getUsuarioById(usuarioId)
            if (usuario == null) return Result.failure(ContenidoNoEncontradoException())

            Result.success(usuario.toUsuarioUi().copy(esMiPerfil = true))
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "getMiPerfil: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "getMiPerfil: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "getMiPerfil: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Sus resenas en vivo, la ultima publicada o editada primero, con mis likes
    fun getReviewsDeUsuarioLive(id: String): Flow<List<ReviewUi>> {
        val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

        return usuarioRemoteDataSource.listenReviewsDeUsuario(id, currentUserId = usuarioActualId)
            .map { reviews ->
                // Las fechas van en formato ISO, asi que ordenar el texto ordena las fechas
                reviews.map { it.toReviewUi(usuarioActualId = usuarioActualId) }.sortedByDescending { it.updatedAt }
            }
            .catch { e ->
                Log.d("UsuarioRepository", "getReviewsDeUsuarioLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Los favoritos son los items a los que le dio like, el mas nuevo primero: albumes, canciones y
    // artistas mezclados. Tambien alimentan el For you del inicio
    fun getFavoritosLive(userId: String): Flow<List<ItemResumenUi>> =
        usuarioRemoteDataSource.listenLikes(userId)
            .map { likes -> likes.map { it.toItemResumenUi() } }
            .catch { e ->
                Log.d("UsuarioRepository", "getFavoritosLive: ${e.message}")
                throw errorDelFlujo(e)
            }

    fun getMisFavoritosLive(): Flow<List<ItemResumenUi>> =
        getFavoritosLive(userId = authRemoteDataSource.currentUser?.uid ?: "")

    // Lo que guarde con el boton de guardar del detalle, el mas nuevo primero
    fun getMisGuardadosLive(): Flow<List<ItemResumenUi>> =
        usuarioRemoteDataSource.listenGuardados(authRemoteDataSource.currentUser?.uid ?: "")
            .map { guardados -> guardados.map { it.toItemResumenUi() } }
            .catch { e ->
                Log.d("UsuarioRepository", "getMisGuardadosLive: ${e.message}")
                throw errorDelFlujo(e)
            }

    // Los usuarios cuyo nombre empieza por el texto. Yo no salgo en la busqueda
    suspend fun buscarUsuarios(texto: String): Result<List<UsuarioUi>> {
        return try {
            val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

            val usuarios = usuarioRemoteDataSource.searchUsuarios(texto)
                .map { it.toUsuarioUi() }
                .filter { it.id != usuarioActualId }
            Result.success(usuarios)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "buscarUsuarios: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "buscarUsuarios: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "buscarUsuarios: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Primero se crea la cuenta en Authentication y despues, con su uid, se guardan los demas datos.
    // Tambien completa el perfil de una cuenta creada antes del sprint 10, que no tiene documento
    suspend fun registrarUsuario(username: String, nombre: String, bio: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val registrarUsuarioDto = RegistrarUsuarioDto(
                username = username,
                nombre = nombre,
                bio = if (bio.isEmpty()) null else bio,
                // La foto se sube a Storage y queda en Authentication; se copia para que otros la vean
                fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
                FCMToken = tokenDelCelular(),
            )
            usuarioRemoteDataSource.registrarUsuario(registrarUsuarioDto, usuarioId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "registrarUsuario: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "registrarUsuario: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "registrarUsuario: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Solo cambian el nombre, el usuario y la biografia: los contadores y la foto se conservan
    suspend fun actualizarPerfil(nombre: String, username: String, bio: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val actualizarUsuarioDto = ActualizarUsuarioDto(
                nombre = nombre,
                username = username,
                bio = if (bio.isEmpty()) null else bio,
            )
            usuarioRemoteDataSource.actualizarPerfil(usuarioId, actualizarUsuarioDto)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "actualizarPerfil: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "actualizarPerfil: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "actualizarPerfil: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Al cambiar la foto tambien se actualiza el documento, para que se vea en mi perfil publico.
    // Ese cambio es el que escucha la Cloud Function que pone la foto nueva en mis resenas
    suspend fun actualizarFotoDePerfil(fotoUrl: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            usuarioRemoteDataSource.actualizarFotoDePerfil(usuarioId, fotoUrl)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "actualizarFotoDePerfil: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "actualizarFotoDePerfil: ${e.message}")
            // Sin documento todavia: la foto se copia cuando complete su perfil en Editar perfil
            if (e.code == FirebaseFirestoreException.Code.NOT_FOUND) Result.success(Unit)
            else Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "actualizarFotoDePerfil: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Al iniciar sesion se guarda el token de este celular: asi las notificaciones llegan aqui
    // aunque la cuenta se haya creado en otro
    suspend fun actualizarToken(): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            // Si la cuenta no tiene documento se crea aqui, asi despues se puede seguir a otros
            crearMiDocumentoSiFalta(usuarioId = usuarioId)

            val fcmToken = messagingRemoteDataSource.getToken()
            usuarioRemoteDataSource.actualizarToken(usuarioId, fcmToken)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "actualizarToken: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "actualizarToken: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "actualizarToken: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Las listas en vivo: si alguien empieza a seguirlo desde otro celular, aparece sin salir
    fun getSeguidoresLive(userId: String): Flow<List<UsuarioUi>> =
        usuarioRemoteDataSource.listenSeguidores(userId)
            .map { seguidores -> seguidores.map { it.toUsuarioUi() } }
            .catch { e ->
                Log.d("UsuarioRepository", "getSeguidoresLive: ${e.message}")
                throw errorDelFlujo(e)
            }

    fun getSeguidosLive(userId: String): Flow<List<UsuarioUi>> =
        usuarioRemoteDataSource.listenSeguidos(userId)
            .map { seguidos -> seguidos.map { it.toUsuarioUi() } }
            .catch { e ->
                Log.d("UsuarioRepository", "getSeguidosLive: ${e.message}")
                throw errorDelFlujo(e)
            }

    // Los usuarios que sigo, para el filtro de seguidos del inicio
    fun getMisSeguidosLive(): Flow<List<UsuarioUi>> =
        getSeguidosLive(userId = authRemoteDataSource.currentUser?.uid ?: "")

    // Una cuenta de Authentication puede quedar sin documento en usuarios: el registro entra aunque
    // falle Firestore y, en modo debug, la sesion sigue en el celular aunque el emulador se reinicie
    // vacio. Sin el documento no se puede seguir ni guardar el token, asi que se crea igual que en el
    // registro, con la parte del correo antes de la arroba como usuario
    private suspend fun crearMiDocumentoSiFalta(usuarioId: String) {
        if (usuarioRemoteDataSource.getUsuarioById(usuarioId) != null) return

        val usuarioDelCorreo = authRemoteDataSource.currentUser?.email?.substringBefore("@") ?: ""
        val nombre = authRemoteDataSource.currentUser?.displayName

        val registrarUsuarioDto = RegistrarUsuarioDto(
            username = usuarioDelCorreo,
            nombre = if (nombre.isNullOrEmpty()) usuarioDelCorreo else nombre,
            fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
            FCMToken = tokenDelCelular(),
        )
        usuarioRemoteDataSource.registrarUsuario(registrarUsuarioDto, usuarioId)
    }

    // Va en su propio try: si el token falla, el documento queda con "Vacio" y lo demas sigue
    private suspend fun tokenDelCelular(): String =
        try {
            messagingRemoteDataSource.getToken()
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "tokenDelCelular: ${e.message}")
            "Vacio"
        }
}
