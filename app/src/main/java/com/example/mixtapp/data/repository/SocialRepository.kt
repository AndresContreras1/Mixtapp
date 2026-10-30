package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.SocialLocalDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.toFollowingReviewUi
import com.example.mixtapp.data.model.FollowingStoryUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.UsuarioUi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

// La pestana Seguidos sale de la base remota; las notificaciones de la pantalla siguen locales.
// Recibe los contratos: Hilt entrega las implementaciones desde DataSourceModule
class SocialRepository @Inject constructor(
    private val socialLocalDataSource: SocialLocalDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val albumRemoteDataSource: AlbumRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Seguidos en vivo con dos listeners: mi documento cambia al seguir o dejar de seguir (sus
    // contadores) y las resenas cambian con cada resena nueva o like. Cuando llega cualquiera de los
    // dos, combine arma la pestana de nuevo. Como en clase, retorna el flujo y no un Result
    fun getFollowingLive(): Flow<FollowingUi> {
        val usuarioId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            usuarioRemoteDataSource.listenUsuarioById(usuarioId),
            albumRemoteDataSource.listenAllReviews(),
        ) { miPerfil, _ -> armarFollowing(usuarioId = usuarioId, miPerfil = miPerfil.toUsuarioUi()) }
            .catch { e ->
                Log.d("SocialRepository", "getFollowingLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Las resenas de los usuarios que sigo, la mas reciente primero, con mis contadores. Si falla
    // una consulta falla todo: una lista a medias mostraria que alguien no ha escrito nada
    private suspend fun armarFollowing(usuarioId: String, miPerfil: UsuarioUi): FollowingUi =
        coroutineScope {
            val seguidos = usuarioRemoteDataSource.getSeguidos(usuarioId).map { it.toUsuarioUi() }

            // Las resenas de cada usuario que sigo tambien se piden a la vez, con mis likes
            val pendientes = seguidos.map { usuario ->
                async { usuarioRemoteDataSource.getReviewsDeUsuario(usuario.id, currentUserId = usuarioId) }
            }

            val reviews = pendientes.map { it.await() }.flatten()
                .map { dto -> dto.toFollowingReviewUi() }
                // La fecha va como AAAA-MM-DD, asi que ordenar el texto ordena las fechas
                .sortedByDescending { it.reviewedAt }

            val stories = listOf(
                FollowingStoryUi(id = "log", label = "", initials = "+", isAddAction = true, fotoUrl = null)
            ) + seguidos.map { usuario ->
                val nombre = usuario.nombre.ifEmpty { usuario.username }

                FollowingStoryUi(
                    id = usuario.id,
                    label = nombre,
                    initials = nombre.take(2).uppercase(),
                    isAddAction = false,
                    fotoUrl = usuario.fotoUrl,
                )
            }

            FollowingUi(
                id = usuarioId,
                followingCount = miPerfil.seguidos,
                followersCount = miPerfil.seguidores,
                stories = stories,
                reviews = reviews,
            )
        }

    suspend fun getNotifications(): Result<List<NotificationUi>> {
        return try {
            Result.success(socialLocalDataSource.getNotifications())
        } catch (e: Exception) {
            Log.d("SocialRepository", "getNotifications: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }
}
