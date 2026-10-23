package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.ActualizarUsuarioDto
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.toMyReviewUi
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.UsuarioUi
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import javax.inject.Inject

// Para volver al backend de Express basta con cambiar la implementacion a UsuarioRetrofitDataSourceImpl.
// El backend todavia no tiene rutas para guardar usuarios ni seguidores, asi que esas funciones
// quedan como to-do en esa implementacion, como en clase
class UsuarioRepository @Inject constructor(
    private val usuarioRemoteDataSource: UsuarioFirestoreDataSourceImpl,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // El perfil en vivo: si alguien lo sigue desde otro celular, el contador cambia sin salir.
    // Con mi uid el data source dice si ya lo sigo
    fun getUsuarioByIdLive(id: String): Flow<UsuarioUi> {
        val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

        return usuarioRemoteDataSource.listenUsuarioById(id, currentUserId = usuarioActualId)
            .map { dto ->
                val usuario = dto.toUsuarioUi()
                usuario.copy(esMiPerfil = usuario.id == usuarioActualId)
            }
            .catch { e ->
                Log.d("UsuarioRepository", "getUsuarioByIdLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Sigue o deja de seguir: el data source decide segun si ya lo sigo. La UI cambia el boton
    // y el contador solo si esto sale bien
    suspend fun followOrUnfollow(targetUserId: String): Result<Unit> {
        return try {
            val usuarioActualId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            // Nadie se sigue a si mismo
            if (targetUserId == usuarioActualId) return Result.failure(ErrorInesperadoException())

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
            .map { dto -> dto.toUsuarioUi().copy(esMiPerfil = true) }
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

            val usuario = usuarioRemoteDataSource.getUsuarioById(usuarioId).toUsuarioUi()
            Result.success(usuario.copy(esMiPerfil = true))
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "getMiPerfil: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "getMiPerfil: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: NoSuchElementException) {
            Log.d("UsuarioRepository", "getMiPerfil: ${e.message}")
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "getMiPerfil: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    fun getReviewsDeUsuarioLive(id: String): Flow<List<MyReviewUi>> =
        usuarioRemoteDataSource.listenReviewsDeUsuario(id)
            .map { reviews -> reviews.map { it.toMyReviewUi() } }
            .catch { e ->
                Log.d("UsuarioRepository", "getReviewsDeUsuarioLive: ${e.message}")
                throw errorDelFlujo(e)
            }

    // Primero se crea la cuenta en Authentication y despues, con su uid, se guardan los demas datos.
    // Tambien completa el perfil de una cuenta creada antes del sprint 10, que no tiene documento
    suspend fun registrarUsuario(username: String, nombre: String, bio: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            // Sin el await sigue derecho y el token queda vacio
            val fcmToken = FirebaseMessaging.getInstance().token.await()

            val registrarUsuarioDto = RegistrarUsuarioDto(
                username = username,
                nombre = nombre,
                bio = if (bio.isEmpty()) null else bio,
                // La foto se sube a Storage y queda en Authentication; se copia para que otros la vean
                fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
                FCMToken = fcmToken,
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

            val fcmToken = FirebaseMessaging.getInstance().token.await()
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
}
