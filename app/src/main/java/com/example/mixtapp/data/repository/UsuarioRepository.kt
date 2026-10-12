package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.data.dtos.toMyReviewUi
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.UsuarioUi
import com.google.firebase.firestore.FirebaseFirestoreException
import retrofit2.HttpException
import javax.inject.Inject

// Para volver al backend de Express basta con cambiar la implementacion a UsuarioRetrofitDataSourceImpl
class UsuarioRepository @Inject constructor(
    private val usuarioRemoteDataSource: UsuarioFirestoreDataSourceImpl,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    suspend fun getUsuarioById(id: String): Result<UsuarioUi> {
        return try {
            val usuario = usuarioRemoteDataSource.getUsuarioById(id)
            Result.success(usuario.toUsuarioUi())
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
            Result.failure(e)
        }
    }

    // Mi perfil es el documento que tiene el uid de Authentication como id
    suspend fun getMiPerfil(): Result<UsuarioUi> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val usuario = usuarioRemoteDataSource.getUsuarioById(usuarioId)
            Result.success(usuario.toUsuarioUi())
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
            Result.failure(e)
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
            Result.failure(e)
        }
    }

    // Primero se crea la cuenta en Authentication y despues, con su uid, se guardan los demas datos
    suspend fun registrarUsuario(username: String, nombre: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val registrarUsuarioDto = RegistrarUsuarioDto(
                username = username,
                nombre = nombre,
                bio = null,
                fotoUrl = null,
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
            Result.failure(e)
        }
    }

    // set reemplaza el documento entero, asi que se mandan todos los campos del perfil
    suspend fun actualizarPerfil(nombre: String, username: String, bio: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())
            // La foto se sube a Storage y queda en Authentication; se copia para que otros la vean
            val fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString()

            val registrarUsuarioDto = RegistrarUsuarioDto(
                username = username,
                nombre = nombre,
                bio = if (bio.isEmpty()) null else bio,
                fotoUrl = fotoUrl,
            )
            usuarioRemoteDataSource.registrarUsuario(registrarUsuarioDto, usuarioId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "actualizarPerfil: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "actualizarPerfil: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "actualizarPerfil: ${e.message}")
            Result.failure(e)
        }
    }

    // Al cambiar la foto tambien se actualiza el documento, para que se vea en mi perfil publico
    suspend fun actualizarFotoDePerfil(fotoUrl: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())
            val actual = usuarioRemoteDataSource.getUsuarioById(usuarioId).toUsuarioUi()

            val registrarUsuarioDto = RegistrarUsuarioDto(
                username = actual.username,
                nombre = actual.nombre,
                bio = if (actual.bio.isEmpty()) null else actual.bio,
                fotoUrl = fotoUrl,
            )
            usuarioRemoteDataSource.registrarUsuario(registrarUsuarioDto, usuarioId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "actualizarFotoDePerfil: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("UsuarioRepository", "actualizarFotoDePerfil: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: NoSuchElementException) {
            // Sin documento todavia: la foto se copia cuando complete su perfil en Editar perfil
            Log.d("UsuarioRepository", "actualizarFotoDePerfil: ${e.message}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "actualizarFotoDePerfil: ${e.message}")
            Result.failure(e)
        }
    }
}
