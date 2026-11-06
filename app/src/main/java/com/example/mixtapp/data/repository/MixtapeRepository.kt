package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.toItemMixtapeDto
import com.example.mixtapp.data.dtos.toMixtapeUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.MixtapeUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MixtapeRepository @Inject constructor(
    private val mixtapeRemoteDataSource: MixtapeRemoteDataSource,
    private val authRepository: AuthRepository,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
) {
    fun obtenerMixtapesDeUsuario(usuarioId: String): Flow<List<MixtapeUi>> {
        val currentUid = authRepository.currentUser?.uid ?: ""
        return mixtapeRemoteDataSource.escucharMixtapesDelUsuario(
            usuarioId = usuarioId,
            currentUserId = currentUid,
        ).map { lista ->
            lista.map { it.toMixtapeUi(usuarioActualId = currentUid) }
        }
    }

    fun obtenerMisMixtapes(): Flow<List<MixtapeUi>> {
        val currentUid = authRepository.currentUser?.uid ?: ""
        return obtenerMixtapesDeUsuario(usuarioId = currentUid)
    }

    fun obtenerMixtapePorId(id: String): Flow<MixtapeUi?> {
        val currentUid = authRepository.currentUser?.uid ?: ""
        return mixtapeRemoteDataSource.escucharMixtapePorId(
            id = id,
            currentUserId = currentUid,
        ).map { dto ->
            dto?.toMixtapeUi(usuarioActualId = currentUid)
        }
    }

    suspend fun crearMixtape(
        nombre: String,
        descripcion: String?,
        primerItem: ItemResumenUi? = null
    ): Result<String> {
        val currentUser = authRepository.currentUser
            ?: return Result.failure(SinSesionException())

        val usuarioProfile = usuarioRemoteDataSource.getUsuarioById(currentUser.uid)?.toUsuarioUi()
        val usuarioNombre = usuarioProfile?.nombre ?: currentUser.displayName ?: currentUser.email?.substringBefore("@") ?: "Usuario"
        val usuarioFotoUrl = usuarioProfile?.fotoUrl ?: currentUser.photoUrl?.toString()

        return try {
            val id = mixtapeRemoteDataSource.crearMixtape(
                nombre = nombre.trim(),
                descripcion = descripcion?.trim(),
                usuarioId = currentUser.uid,
                usuarioNombre = usuarioNombre,
                usuarioFotoUrl = usuarioFotoUrl,
                primerItem = primerItem?.toItemMixtapeDto()
            )
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(traducirExcepcion(e))
        }
    }

    suspend fun agregarItemAMixtape(mixtapeId: String, item: ItemResumenUi): Result<Unit> {
        return try {
            mixtapeRemoteDataSource.agregarItemAMixtape(mixtapeId, item.toItemMixtapeDto())
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(traducirExcepcion(e))
        }
    }

    suspend fun eliminarItemDeMixtape(mixtapeId: String, itemId: String): Result<Unit> {
        return try {
            mixtapeRemoteDataSource.eliminarItemDeMixtape(mixtapeId, itemId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(traducirExcepcion(e))
        }
    }

    suspend fun eliminarMixtape(mixtapeId: String): Result<Unit> {
        return try {
            mixtapeRemoteDataSource.eliminarMixtape(mixtapeId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(traducirExcepcion(e))
        }
    }

    suspend fun toggleLikeMixtape(mixtapeId: String): Result<Unit> {
        val currentUser = authRepository.currentUser
            ?: return Result.failure(SinSesionException())
        return try {
            mixtapeRemoteDataSource.toggleLikeMixtape(mixtapeId, currentUser.uid)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(traducirExcepcion(e))
        }
    }

    private fun traducirExcepcion(e: Exception): Exception = when (e) {
        is FirebaseFirestoreException -> errorDeFirestore(e)
        is HttpException -> errorDelBackend(e)
        else -> ErrorInesperadoException()
    }
}
