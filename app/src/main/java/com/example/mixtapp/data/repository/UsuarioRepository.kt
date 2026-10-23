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
import retrofit2.HttpException
import javax.inject.Inject

// Para volver al backend de Express basta con cambiar la implementacion a UsuarioRetrofitDataSourceImpl.
// El backend todavia no tiene rutas para guardar usuarios ni seguidores, asi que esas funciones
// quedan como to-do en esa implementacion, como en clase
class UsuarioRepository @Inject constructor(
    private val usuarioRemoteDataSource: UsuarioFirestoreDataSourceImpl,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Con mi uid el data source dice si ya sigo a este usuario
    suspend fun getUsuarioById(id: String): Result<UsuarioUi> {
        return try {
            val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""
            val usuario = usuarioRemoteDataSource.getUsuarioById(id, currentUserId = usuarioActualId).toUsuarioUi()

            Result.success(usuario.copy(esMiPerfil = usuario.id == usuarioActualId))
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "getUsuarioById: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "getUsuarioById: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: NoSuchElementException) {
            Log.d("UsuarioRepository", "getUsuarioById: ${e.message}")
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "getUsuarioById: ${e.message}")
            Result.failure(ErrorInesperadoException())
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

    suspend fun getReviewsDeUsuario(id: String): Result<List<MyReviewUi>> {
        return try {
            val reviews = usuarioRemoteDataSource.getReviewsDeUsuario(id)
            val resenas = reviews.map { it.toMyReviewUi() }
            Result.success(resenas)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "getReviewsDeUsuario: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "getReviewsDeUsuario: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "getReviewsDeUsuario: ${e.message}")
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

    suspend fun getSeguidores(userId: String): Result<List<UsuarioUi>> {
        return try {
            val seguidores = usuarioRemoteDataSource.getSeguidores(userId)
            Result.success(seguidores.map { it.toUsuarioUi() })
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "getSeguidores: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "getSeguidores: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "getSeguidores: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    suspend fun getSeguidos(userId: String): Result<List<UsuarioUi>> {
        return try {
            val seguidos = usuarioRemoteDataSource.getSeguidos(userId)
            Result.success(seguidos.map { it.toUsuarioUi() })
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "getSeguidos: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "getSeguidos: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "getSeguidos: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Los usuarios que sigo, para el filtro de seguidos del inicio
    suspend fun getMisSeguidos(): Result<List<UsuarioUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid
            ?: return Result.failure(SinSesionException())

        return getSeguidos(userId = usuarioId)
    }
}
