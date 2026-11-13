package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.CreateMixtapeDto
import com.example.mixtapp.data.dtos.toItemMixtapeDto
import com.example.mixtapp.data.dtos.toMixtapeUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MixtapeUi
import com.example.mixtapp.data.model.toItemResumenUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.time.Instant
import javax.inject.Inject

// Organiza las mixtapes: su data source, el usuario que se copia en cada una y Authentication para
// saber quien es el usuario de la app
class MixtapeRepository @Inject constructor(
    private val mixtapeRemoteDataSource: MixtapeRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Mis mixtapes en vivo: una nueva, o un item agregado desde el detalle, llegan solos
    fun getMisMixtapesLive(): Flow<List<MixtapeUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return mixtapeRemoteDataSource.listenMixtapesDeUsuario(usuarioId)
            .map { mixtapes -> mixtapes.map { it.toMixtapeUi(usuarioActualId = usuarioId) } }
            .catch { e ->
                Log.d("MixtapeRepository", "getMisMixtapesLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Una mixtape en vivo. El data source envia null si no existe: el repositorio decide el error
    fun getMixtapeLive(id: String): Flow<MixtapeUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return mixtapeRemoteDataSource.listenMixtapeById(id)
            .map { dto -> dto?.toMixtapeUi(usuarioActualId = usuarioId) ?: throw ContenidoNoEncontradoException() }
            .catch { e ->
                Log.d("MixtapeRepository", "getMixtapeLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // La mixtape guarda una copia de mi nombre y mi foto, como la resena
    suspend fun crearMixtape(nombre: String, descripcion: String): Result<String> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val createMixtapeDto = CreateMixtapeDto(
                nombre = nombre,
                // Sin descripcion se guarda null, no un texto vacio
                descripcion = descripcion.ifEmpty { null },
                usuarioId = usuarioId,
                usuarioNombre = nombreDeLaSesion(
                    usuarioRemoteDataSource = usuarioRemoteDataSource,
                    authRemoteDataSource = authRemoteDataSource,
                    usuarioId = usuarioId,
                ),
                usuarioFotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
                items = emptyList(),
                // En formato ISO, para ordenar mis mixtapes por esta fecha
                createdAt = Instant.now().toString(),
            )

            Result.success(mixtapeRemoteDataSource.crearMixtape(mixtape = createMixtapeDto))
        } catch (e: HttpException) {
            Log.d("MixtapeRepository", "crearMixtape: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("MixtapeRepository", "crearMixtape: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("MixtapeRepository", "crearMixtape: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Se copia lo que se muestra del item. La mixtape cambia sola por el listener
    suspend fun agregarItem(mixtapeId: String, item: ItemUi): Result<Unit> {
        return try {
            if (authRemoteDataSource.currentUser == null) return Result.failure(SinSesionException())

            mixtapeRemoteDataSource.agregarItem(mixtapeId = mixtapeId, item = item.toItemResumenUi().toItemMixtapeDto())
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("MixtapeRepository", "agregarItem: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("MixtapeRepository", "agregarItem: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("MixtapeRepository", "agregarItem: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    suspend fun quitarItem(mixtapeId: String, itemId: String): Result<Unit> {
        return try {
            if (authRemoteDataSource.currentUser == null) return Result.failure(SinSesionException())

            mixtapeRemoteDataSource.quitarItem(mixtapeId = mixtapeId, itemId = itemId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("MixtapeRepository", "quitarItem: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("MixtapeRepository", "quitarItem: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("MixtapeRepository", "quitarItem: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    suspend fun eliminarMixtape(mixtapeId: String): Result<Unit> {
        return try {
            if (authRemoteDataSource.currentUser == null) return Result.failure(SinSesionException())

            mixtapeRemoteDataSource.eliminarMixtape(mixtapeId = mixtapeId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("MixtapeRepository", "eliminarMixtape: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("MixtapeRepository", "eliminarMixtape: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("MixtapeRepository", "eliminarMixtape: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }
}
