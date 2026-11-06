package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.DueloRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.CreateDueloDto
import com.example.mixtapp.data.dtos.CreateReviewUsuarioDto
import com.example.mixtapp.data.dtos.CreateVotoDto
import com.example.mixtapp.data.dtos.toItemDelDueloDto
import com.example.mixtapp.data.dtos.toItemResumenUi
import com.example.mixtapp.data.dtos.toVotoUi
import com.example.mixtapp.data.model.DueloEnVivoUi
import com.example.mixtapp.data.model.DueloUi
import com.example.mixtapp.data.model.OPCION_A
import com.example.mixtapp.data.model.OPCION_B
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import retrofit2.HttpException
import java.time.Instant
import javax.inject.Inject

// Organiza los duelos del dia: el duelo con sus votos, el usuario que se copia en cada voto y
// Authentication para saber quien es el usuario de la app
class DueloRepository @Inject constructor(
    private val dueloRemoteDataSource: DueloRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // El duelo en vivo con dos listeners: el documento (los contadores) y sus votos (las
    // opiniones). Cuando llega cualquiera de los dos, combine lo arma de nuevo
    fun getDueloLive(dueloId: String, tipo: String): Flow<DueloEnVivoUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            dueloRemoteDataSource.listenDuelo(dueloId),
            dueloRemoteDataSource.listenVotos(dueloId),
        ) { duelo, votos ->
            // Un duelo en el que nadie ha votado no existe todavia: no es un error, va en 0
            DueloEnVivoUi(
                genero = duelo?.genero,
                votosA = duelo?.votosA ?: 0,
                votosB = duelo?.votosB ?: 0,
                itemA = duelo?.itemA?.toItemResumenUi(tipo = tipo),
                itemB = duelo?.itemB?.toItemResumenUi(tipo = tipo),
                opiniones = votos.map { it.toVotoUi(usuarioActualId = usuarioId) },
                miOpcion = votos.find { it.id == usuarioId }?.opcion,
            )
        }
            .catch { e ->
                Log.d("DueloRepository", "getDueloLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // El voto guarda una copia de mi nombre y mi foto, como la resena. Si soy el primero del dia,
    // el data source crea el duelo con los dos items que estoy viendo
    suspend fun votar(duelo: DueloUi, opcion: String, opinion: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val createDueloDto = CreateDueloDto(
                dia = duelo.dia,
                tipo = duelo.tipo,
                genero = duelo.genero,
                itemA = duelo.itemA.toItemDelDueloDto(),
                itemB = duelo.itemB.toItemDelDueloDto(),
                votosA = if (opcion == OPCION_A) 1 else 0,
                votosB = if (opcion == OPCION_B) 1 else 0,
                createdAt = Instant.now().toString(),
            )
            val createVotoDto = CreateVotoDto(
                opcion = opcion,
                opinion = opinion,
                // En formato ISO, para que las opiniones se ordenen por esta fecha
                createdAt = Instant.now().toString(),
                usuario = CreateReviewUsuarioDto(
                    nombre = nombreDeLaSesion(
                        usuarioRemoteDataSource = usuarioRemoteDataSource,
                        authRemoteDataSource = authRemoteDataSource,
                        usuarioId = usuarioId,
                    ),
                    fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
                ),
            )

            dueloRemoteDataSource.votar(
                dueloId = duelo.id,
                duelo = createDueloDto,
                usuarioId = usuarioId,
                voto = createVotoDto,
            )
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("DueloRepository", "votar: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("DueloRepository", "votar: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("DueloRepository", "votar: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }
}
