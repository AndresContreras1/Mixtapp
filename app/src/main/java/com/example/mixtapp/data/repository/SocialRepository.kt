package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.NotificacionRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.toNotificationUi
import com.example.mixtapp.data.dtos.toReviewUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.UsuarioUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.time.LocalDate
import javax.inject.Inject

// Lo social: la pestana Siguiendo y las notificaciones. Recibe los contratos: Hilt entrega las
// implementaciones desde DataSourceModule
class SocialRepository @Inject constructor(
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val reviewRemoteDataSource: ReviewRemoteDataSource,
    private val notificacionRemoteDataSource: NotificacionRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Siguiendo en vivo con dos listeners: mi documento cambia al seguir o dejar de seguir (sus
    // contadores) y las resenas cambian con cada resena nueva o like. Cuando llega cualquiera de los
    // dos, combine arma la pestana de nuevo. Como en clase, retorna el flujo y no un Result
    fun getFollowingLive(): Flow<FollowingUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            usuarioRemoteDataSource.listenUsuarioById(usuarioId),
            reviewRemoteDataSource.listenAllReviews(),
        ) { miPerfil, _ -> armarFollowing(usuarioId = usuarioId, miPerfil = miPerfil?.toUsuarioUi()) }
            .catch { e ->
                Log.d("SocialRepository", "getFollowingLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Las resenas de los usuarios que sigo, la ultima publicada o editada primero, con mis likes.
    // Si mi cuenta todavia no tiene documento, los contadores quedan en 0. Si falla una consulta
    // falla todo: una lista a medias mostraria que alguien no ha escrito nada
    private suspend fun armarFollowing(usuarioId: String, miPerfil: UsuarioUi?): FollowingUi =
        coroutineScope {
            val seguidos = usuarioRemoteDataSource.getSeguidos(usuarioId).map { it.toUsuarioUi() }

            // Las resenas de cada usuario que sigo tambien se piden a la vez, con mis likes
            val pendientes = seguidos.map { usuario ->
                async { usuarioRemoteDataSource.getReviewsDeUsuario(usuario.id, currentUserId = usuarioId) }
            }

            val reviews = pendientes.map { it.await() }.flatten()
                .map { dto -> dto.toReviewUi(usuarioActualId = usuarioId) }
                // Las fechas van en formato ISO, asi que ordenar el texto ordena las fechas
                .sortedByDescending { it.updatedAt }

            FollowingUi(
                id = usuarioId,
                followingCount = miPerfil?.seguidos ?: 0,
                followersCount = miPerfil?.seguidores ?: 0,
                seguidos = seguidos,
                reviews = reviews,
            )
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
