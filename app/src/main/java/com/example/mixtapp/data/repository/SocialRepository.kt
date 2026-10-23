package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewLocalDataSource
import com.example.mixtapp.data.datasource.SocialLocalDataSource
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.toFollowingReviewUi
import com.example.mixtapp.data.model.FollowingStoryUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.NotificationUi
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import retrofit2.HttpException
import javax.inject.Inject

// La pestana Seguidos sale de la base remota; las notificaciones de la pantalla siguen locales
class SocialRepository @Inject constructor(
    private val db: com.google.firebase.firestore.FirebaseFirestore,
    private val socialLocalDataSource: SocialLocalDataSource,
    private val reviewLocalDataSource: ReviewLocalDataSource,
    private val usuarioRemoteDataSource: UsuarioFirestoreDataSourceImpl,
    private val authRemoteDataSource: AuthRemoteDataSource
) {
    fun observeFollowing(): kotlinx.coroutines.flow.Flow<Result<FollowingUi>> =
        kotlinx.coroutines.flow.combine(
            observeChanges(db.collection("reviews")) { Result.success(Unit) },
            observeChanges(db.collection("usuarios").whereEqualTo(com.google.firebase.firestore.FieldPath.documentId(), authRemoteDataSource.currentUser?.uid ?: "sin-sesion")) { Result.success(Unit) }
        ) { reviews, profile ->
            val error = reviews.exceptionOrNull() ?: profile.exceptionOrNull()
            if (error != null) Result.failure(error) else getFollowing()
        }


    // Las resenas de los usuarios que sigo, la mas reciente primero, con mis contadores. Si falla
    // una consulta falla todo: una lista a medias mostraria que alguien no ha escrito nada
    suspend fun getFollowing(): Result<FollowingUi> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val following = coroutineScope {
                // Mis contadores y los usuarios que sigo salen a la vez
                val perfil = async { usuarioRemoteDataSource.getUsuarioById(usuarioId).toUsuarioUi() }
                val usuarios = async { usuarioRemoteDataSource.getSeguidos(usuarioId).map { it.toUsuarioUi() } }
                val miPerfil = perfil.await()
                val seguidos = usuarios.await()

                // Las resenas de cada usuario que sigo tambien se piden a la vez, con mis likes
                val pendientes = seguidos.map { usuario ->
                    async { usuarioRemoteDataSource.getReviewsDeUsuario(usuario.id, currentUserId = usuarioId) }
                }

                val reviews = pendientes.map { it.await() }.flatten()
                    .map { dto ->
                        dto.toFollowingReviewUi(comentarios = reviewLocalDataSource.getComentarios(reviewId = dto.id).size)
                    }
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

            Result.success(following)
        } catch (e: HttpException) {
            Log.d("SocialRepository", "getFollowing: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("SocialRepository", "getFollowing: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: NoSuchElementException) {
            Log.d("SocialRepository", "getFollowing: ${e.message}")
            Result.failure(ContenidoNoEncontradoException())
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d("SocialRepository", "getFollowing: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    suspend fun getNotifications(): Result<List<NotificationUi>> {
        return try {
            Result.success(socialLocalDataSource.getNotifications())
        } catch (e: kotlinx.coroutines.CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.d("SocialRepository", "getNotifications: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }
}
