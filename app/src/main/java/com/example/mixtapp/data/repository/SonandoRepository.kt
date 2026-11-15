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

    // Dos listeners: lo que suena desde hace 12 horas y a quienes sigo. Cuando llega cualquiera de
    // los dos, combine arma la fila otra vez: lo mio primero y despues lo de quienes sigo. reloj da
    // la hora actual; las pruebas pasan una fija
    fun getSonandoLive(reloj: () -> Instant = { Instant.now() }): Flow<List<SonandoUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            sonandoRemoteDataSource.listenSonando(desde = hace12Horas(reloj()), currentUserId = usuarioId),
            usuarioRemoteDataSource.listenSeguidos(usuarioId),
        ) { sonando, seguidos ->
            // Con cada cambio se vuelve a mirar la hora: lo que ya paso de 12 horas sale de la
            // fila y el "hace cuanto" avanza
            val ahora = reloj()
            val limite = hace12Horas(ahora)
            val seguidosIds = seguidos.map { it.toUsuarioUi().id }.toSet()

            sonando
                .filter { dto -> dto.id == usuarioId || dto.id in seguidosIds }
                .filter { dto -> (dto.actualizadoEn ?: "") >= limite }
                .map { dto -> dto.toSonandoUi(usuarioActualId = usuarioId, ahora = ahora) }
                .sortedByDescending { it.esMio }
        }
            .catch { e ->
                Log.d("SonandoRepository", "getSonandoLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // La fecha desde la que se muestra lo que suena, en formato ISO como actualizadoEn
    private fun hace12Horas(ahora: Instant): String =
        ahora.minusSeconds(HORAS_SONANDO * 60 * 60).toString()

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

    // Deja de mostrar lo que estoy escuchando; sus reacciones se borran con el
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
            if (sonando.usuarioId == usuarioId) return Result.failure(ReaccionarALoMioException())

            val createReaccionDto = CreateReaccionDto(
                usuarioId = usuarioId,
                itemId = sonando.item.id,
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
