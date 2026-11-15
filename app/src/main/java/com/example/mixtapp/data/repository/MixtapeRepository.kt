package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.MixtapeRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.CreateMixtapeDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.toCreateItemMixtapeDto
import com.example.mixtapp.data.dtos.toItemResumenUi
import com.example.mixtapp.data.dtos.toMixtapeUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MixtapeConItemsUi
import com.example.mixtapp.data.model.MixtapeUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
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

    // Mis mixtapes en vivo, la mas nueva primero: una nueva, o un item agregado desde el detalle,
    // llegan solos. Con el id de un item, cada mixtape dice si ya lo tiene
    fun getMisMixtapesLive(itemId: String = ""): Flow<List<MixtapeUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return mixtapeRemoteDataSource.listenMixtapesDeUsuario(usuarioId = usuarioId, itemId = itemId)
            .map { mixtapes ->
                // createdAt va en formato ISO, asi que ordenar el texto ordena las fechas
                mixtapes
                    .sortedByDescending { it.createdAt }
                    .map { it.toMixtapeUi(usuarioActualId = usuarioId) }
            }
            .catch { e ->
                Log.d("MixtapeRepository", "getMisMixtapesLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Una mixtape con sus items en vivo: dos listeners, el documento y su subcoleccion. Cuando llega
    // cualquiera de los dos, combine la arma otra vez. El data source envia null si la mixtape no
    // existe: el repositorio decide el error
    fun getMixtapeLive(id: String): Flow<MixtapeConItemsUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            mixtapeRemoteDataSource.listenMixtapeById(id),
            mixtapeRemoteDataSource.listenItemsDeMixtape(id),
        ) { mixtape, items ->
            if (mixtape == null) throw ContenidoNoEncontradoException()

            MixtapeConItemsUi(
                mixtape = mixtape.toMixtapeUi(usuarioActualId = usuarioId),
                // En el orden en que se agregaron
                items = items.sortedBy { it.agregadoEn }.map { it.toItemResumenUi() },
            )
        }
            .catch { e ->
                Log.d("MixtapeRepository", "getMixtapeLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // La mixtape guarda una copia de mi nombre y mi foto, como la resena. Retorna el id de la nueva
    suspend fun crearMixtape(nombre: String, descripcion: String): Result<String> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val createMixtapeDto = CreateMixtapeDto(
                nombre = nombre,
                // Sin descripcion se guarda null, no un texto vacio
                descripcion = descripcion.ifEmpty { null },
                usuarioId = usuarioId,
                usuario = CreateReviewUsuarioDto(
                    nombre = nombreDeLaSesion(
                        usuarioRemoteDataSource = usuarioRemoteDataSource,
                        authRemoteDataSource = authRemoteDataSource,
                        usuarioId = usuarioId,
                    ),
                    fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
                ),
                itemsCount = 0,
                portadas = emptyList(),
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

    // Se copia lo que se muestra del item. La mixtape cambia sola por el listener. Si la borraron
    // mientras se elegia, el data source responde false y el error es que no se encontro
    suspend fun agregarItem(mixtapeId: String, item: ItemUi): Result<Unit> {
        return try {
            if (authRemoteDataSource.currentUser == null) return Result.failure(SinSesionException())

            val agregado = mixtapeRemoteDataSource.agregarItem(
                mixtapeId = mixtapeId,
                itemId = item.id,
                item = item.toCreateItemMixtapeDto(agregadoEn = Instant.now().toString()),
            )

            if (agregado) Result.success(Unit) else Result.failure(ContenidoNoEncontradoException())
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

    // Quita un item de mi mixtape. Si la mixtape ya no existe, el error es que no se encontro
    suspend fun quitarItem(mixtapeId: String, itemId: String): Result<Unit> {
        return try {
            if (authRemoteDataSource.currentUser == null) return Result.failure(SinSesionException())

            val quitado = mixtapeRemoteDataSource.quitarItem(mixtapeId = mixtapeId, itemId = itemId)

            if (quitado) Result.success(Unit) else Result.failure(ContenidoNoEncontradoException())
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

    // Borra mi mixtape con todos sus items
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
