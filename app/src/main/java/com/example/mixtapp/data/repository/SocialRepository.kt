package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.NotificacionRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.toNotificationUi
import com.example.mixtapp.data.dtos.toReviewUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.UsuarioUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

// Lo social: la pestana Siguiendo y las notificaciones. Recibe los contratos: Hilt entrega las
// implementaciones desde DataSourceModule
class SocialRepository @Inject constructor(
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val notificacionRemoteDataSource: NotificacionRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Siguiendo en vivo: mi documento (sus contadores cambian al seguir o dejar de seguir) y las
    // resenas de los que sigo. Cuando llega cualquiera de los dos, combine arma la pestana de nuevo.
    // Si mi cuenta todavia no tiene documento, los contadores quedan en 0. Como en clase, retorna el
    // flujo y no un Result
    fun getFollowingLive(): Flow<FollowingUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            usuarioRemoteDataSource.listenUsuarioById(usuarioId),
            seguidosConSusResenasLive(usuarioId = usuarioId),
        ) { miPerfil, seguidosConResenas ->
            val perfil = miPerfil?.toUsuarioUi()

            FollowingUi(
                id = usuarioId,
                followingCount = perfil?.seguidos ?: 0,
                followersCount = perfil?.seguidores ?: 0,
                seguidos = seguidosConResenas.seguidos,
                reviews = seguidosConResenas.reviews,
            )
        }
            .catch { e ->
                Log.d("SocialRepository", "getFollowingLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // A quienes sigo y sus resenas, la ultima publicada o editada primero, con mis likes. Solo se
    // escucha lo de ellos: un listener para mi lista de seguidos y uno por cada seguido. Cuando la
    // lista cambia, flatMapLatest deja los listeners de antes y abre los de la lista nueva
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun seguidosConSusResenasLive(usuarioId: String): Flow<SeguidosConResenas> =
        usuarioRemoteDataSource.listenSeguidos(usuarioId)
            .flatMapLatest { seguidosDto ->
                val seguidos = seguidosDto.map { it.toUsuarioUi() }

                // combine de una lista vacia nunca emite: sin seguidos no hay resenas
                if (seguidos.isEmpty()) {
                    flowOf(SeguidosConResenas(seguidos = seguidos, reviews = emptyList()))
                } else {
                    combine(
                        seguidos.map { usuario ->
                            usuarioRemoteDataSource.listenReviewsDeUsuario(usuario.id, currentUserId = usuarioId)
                        }
                    ) { resenasPorSeguido ->
                        SeguidosConResenas(
                            seguidos = seguidos,
                            reviews = resenasPorSeguido.toList().flatten()
                                .map { dto -> dto.toReviewUi(usuarioActualId = usuarioId) }
                                // Las fechas van en formato ISO, asi que ordenar el texto ordena las fechas
                                .sortedByDescending { it.updatedAt },
                        )
                    }
                }
            }

    // Mis notificaciones en vivo: cada like, comentario o seguidor nuevo llega solo, la mas nueva
    // primero. Hoy se compara con la fecha de cada una para agruparlas
    fun getNotificacionesLive(): Flow<List<NotificationUi>> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return notificacionRemoteDataSource.listenNotificaciones(usuarioId)
            .map { notificaciones ->
                val hoy = LocalDate.now().toString()
                notificaciones.map { it.toNotificationUi(hoy = hoy) }
            }
            .catch { e ->
                Log.d("SocialRepository", "getNotificacionesLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // La marca de leida llega de vuelta por el listener
    suspend fun marcarNotificacionLeida(notificacionId: String): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            notificacionRemoteDataSource.marcarLeida(usuarioId = usuarioId, notificacionId = notificacionId)
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("SocialRepository", "marcarNotificacionLeida: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("SocialRepository", "marcarNotificacionLeida: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("SocialRepository", "marcarNotificacionLeida: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }
}

// Mis seguidos con sus resenas: lo que arma el flujo de Siguiendo antes de juntarlo con mi perfil
private data class SeguidosConResenas(
    val seguidos: List<UsuarioUi>,
    val reviews: List<ReviewUi>,
)
