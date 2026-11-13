package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.SonandoRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.CreateReaccionDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.CreateSonandoDto
import com.example.mixtapp.data.dtos.toSonandoUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.SonandoUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import retrofit2.HttpException
import java.time.Instant
import javax.inject.Inject

// Lo que se puso a sonar hace mas de esto ya no sale en la fila
private const val HORAS_SONANDO = 12L

// Organiza "sonando ahora": lo que escuchan los usuarios, mis seguidos para saber cuales mostrar,
// el usuario que se copia y Authentication para saber quien es el usuario de la app
class SonandoRepository @Inject constructor(
    private val sonandoRemoteDataSource: SonandoRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Dos listeners: lo que suena y a quienes sigo. Cuando llega cualquiera de los dos, combine
    // arma la fila otra vez: lo mio primero y despues lo de quienes sigo, de las ultimas 12 horas
    fun getSonandoLive(ahora: Instant = Instant.now()): Flow<List<SonandoUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""
        // Las fechas van en formato ISO: comparar el texto compara las fechas
        val desde = ahora.minusSeconds(HORAS_SONANDO * 60 * 60).toString()

        return combine(
            sonandoRemoteDataSource.listenSonando(currentUserId = usuarioId),
            usuarioRemoteDataSource.listenSeguidos(usuarioId),
        ) { sonando, seguidos ->
            val seguidosIds = seguidos.map { it.toUsuarioUi().id }.toSet()

            sonando
                .filter { dto -> dto.id == usuarioId || dto.id in seguidosIds }
                .filter { dto -> (dto.actualizadoEn ?: "") >= desde }
                .map { dto -> dto.toSonandoUi(usuarioActualId = usuarioId, ahora = ahora) }
                .sortedByDescending { it.esMio }
        }
            .catch { e ->
                Log.d("SonandoRepository", "getSonandoLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Se copia el item y mi nombre y mi foto, como en la resena. Reemplaza lo que estaba sonando
    suspend fun ponerSonando(item: ItemUi): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val createSonandoDto = CreateSonandoDto(
                usuario = CreateReviewUsuarioDto(
                    nombre = nombreDeLaSesion(
                        usuarioRemoteDataSource = usuarioRemoteDataSource,
                        authRemoteDataSource = authRemoteDataSource,
                        usuarioId = usuarioId,
                    ),
                    fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
                ),
                itemId = item.id,
                tipo = item.tipo,
                titulo = item.titulo,
                subtitulo = item.subtitulo,
                imagenUrl = item.imagenUrl,
                reaccionesCount = 0,
                // En formato ISO, para ordenar la fila y saber cuanto hace
                actualizadoEn = Instant.now().toString(),
            )

            sonandoRemoteDataSource.ponerSonando(usuarioId = usuarioId, sonando = createSonandoDto)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("SonandoRepository", "ponerSonando: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("SonandoRepository", "ponerSonando: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("SonandoRepository", "ponerSonando: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    suspend fun quitarSonando(): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            sonandoRemoteDataSource.quitarSonando(usuarioId = usuarioId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("SonandoRepository", "quitarSonando: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("SonandoRepository", "quitarSonando: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("SonandoRepository", "quitarSonando: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // El 🔥 a lo que escucha otro: lo pone o lo quita. El contador cambia cuando llega el
    // documento por el listener, y la Cloud Function le avisa a esa persona
    suspend fun reaccionar(sonando: SonandoUi): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            // A lo mio no se le reacciona
            if (sonando.usuarioId == usuarioId) return Result.failure(SeguirseASiMismoException())

            val createReaccionDto = CreateReaccionDto(
                usuarioId = usuarioId,
                itemId = sonando.item.id,
                nombre = nombreDeLaSesion(
                    usuarioRemoteDataSource = usuarioRemoteDataSource,
                    authRemoteDataSource = authRemoteDataSource,
                    usuarioId = usuarioId,
                ),
                fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
                createdAt = Instant.now().toString(),
            )

            sonandoRemoteDataSource.reaccionar(usuarioId = sonando.usuarioId, reaccion = createReaccionDto)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("SonandoRepository", "reaccionar: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("SonandoRepository", "reaccionar: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("SonandoRepository", "reaccionar: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }
}
