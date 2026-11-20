package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.ComunidadRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.CreateMensajeDto
import com.example.mixtapp.data.dtos.UsuarioDto
import com.example.mixtapp.data.dtos.toChatUi
import com.example.mixtapp.data.dtos.toCopiaDeItemDto
import com.example.mixtapp.data.dtos.toForoUi
import com.example.mixtapp.data.dtos.toItemResumenUi
import com.example.mixtapp.data.dtos.toMensajeUi
import com.example.mixtapp.data.model.AfinidadUi
import com.example.mixtapp.data.model.ChatUi
import com.example.mixtapp.data.model.ForoUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MensajeUi
import com.example.mixtapp.data.model.UsuarioDelChatUi
import com.example.mixtapp.data.model.generoEnComun
import com.example.mixtapp.data.model.idDelChat
import com.example.mixtapp.data.model.porcentajeDeAfinidad
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.time.Instant
import javax.inject.Inject

// Cuantos usuarios se comparan para la afinidad y cuantos foros se ven
private const val USUARIOS_PARA_AFINIDAD = 60
private const val FOROS_EN_LA_LISTA = 30

// La comunidad: con quien tengo afinidad, los foros de cada item y los mensajes directos. Los data
// sources entregan los DTO y aqui se arman las listas y se decide el error
class ComunidadRepository @Inject constructor(
    private val comunidadRemoteDataSource: ComunidadRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource,
) {

    // Las personas con gustos parecidos a los mios, la de mas afinidad primero. Sin mis likes no hay
    // con que comparar y la lista queda vacia. Los likes de cada uno se piden a la vez
    suspend fun getAfinidades(): Result<List<AfinidadUi>> {
        return try {
            val miId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val misLikes = comunidadRemoteDataSource.getLikes(miId).map { it.toItemResumenUi() }
            if (misLikes.isEmpty()) return Result.success(emptyList())

            val otros = comunidadRemoteDataSource.getUsuarios(USUARIOS_PARA_AFINIDAD).filter { it.id != miId }
            val afinidades = coroutineScope {
                otros.map { otro ->
                    async {
                        val susLikes = comunidadRemoteDataSource.getLikes(otro.id).map { it.toItemResumenUi() }
                        val misIds = misLikes.map { it.id }.toSet()
                        AfinidadUi(
                            usuario = otro.toUsuarioUi(),
                            porcentaje = porcentajeDeAfinidad(misLikes = misLikes, susLikes = susLikes),
                            enComun = susLikes.filter { it.id in misIds }.take(3),
                            generoEnComun = generoEnComun(misLikes = misLikes, susLikes = susLikes),
                        )
                    }
                }.awaitAll()
            }

            Result.success(afinidades.filter { it.porcentaje > 0 }.sortedByDescending { it.porcentaje })
        } catch (e: FirebaseFirestoreException) {
            Log.d("ComunidadRepository", "getAfinidades: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("ComunidadRepository", "getAfinidades: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Los foros con actividad, el de la ultima conversacion primero
    fun getForosLive(): Flow<List<ForoUi>> =
        comunidadRemoteDataSource.listenForos(FOROS_EN_LA_LISTA)
            .map { foros -> foros.mapNotNull { it.toForoUi() } }
            .catch { e ->
                Log.d("ComunidadRepository", "getForosLive: ${e.message}")
                throw errorDelFlujo(e)
            }

    fun getMensajesDelForoLive(itemId: String): Flow<List<MensajeUi>> {
        val miId = authRemoteDataSource.currentUser?.uid ?: ""

        return comunidadRemoteDataSource.listenMensajesDelForo(itemId)
            .map { mensajes -> mensajes.map { it.toMensajeUi(miId = miId) } }
            .catch { e ->
                Log.d("ComunidadRepository", "getMensajesDelForoLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // El mensaje lleva la copia de quien lo escribe y el foro guarda la copia del item
    suspend fun publicarEnForo(item: ItemUi, texto: String): Result<Unit> {
        return try {
            val miId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())
            val ahora = Instant.now().toString()

            comunidadRemoteDataSource.publicarEnForo(
                itemId = item.id,
                item = item.toCopiaDeItemDto(createdAt = ahora),
                mensaje = CreateMensajeDto(usuarioId = miId, usuario = miCopia(miId), texto = texto, createdAt = ahora),
            )
            Result.success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Log.d("ComunidadRepository", "publicarEnForo: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("ComunidadRepository", "publicarEnForo: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Mis chats, el de la ultima conversacion primero (Firestore no los ordena: ver el data source)
    fun getChatsLive(): Flow<List<ChatUi>> {
        val miId = authRemoteDataSource.currentUser?.uid ?: ""

        return comunidadRemoteDataSource.listenChats(miId)
            .map { chats ->
                chats.sortedByDescending { it.actualizadoEn ?: "" }.mapNotNull { it.toChatUi(miId = miId) }
            }
            .catch { e ->
                Log.d("ComunidadRepository", "getChatsLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Los mensajes del chat con otra persona: el id del chat sale de los dos uid
    fun getMensajesDelChatLive(otroId: String): Flow<List<MensajeUi>> {
        val miId = authRemoteDataSource.currentUser?.uid ?: ""

        return comunidadRemoteDataSource.listenMensajesDelChat(idDelChat(miId, otroId))
            .map { mensajes -> mensajes.map { it.toMensajeUi(miId = miId) } }
            .catch { e ->
                Log.d("ComunidadRepository", "getMensajesDelChatLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Envia un mensaje a otra persona; el chat se crea con el primero. Escribirse a si mismo no
    // tiene sentido
    suspend fun enviarMensaje(otro: UsuarioDelChatUi, texto: String): Result<Unit> {
        return try {
            val miId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())
            if (otro.id == miId) return Result.failure(MensajeASiMismoException())

            val yo = miCopia(miId)
            comunidadRemoteDataSource.enviarMensaje(
                chatId = idDelChat(miId, otro.id),
                participantes = listOf(yo, UsuarioDto(id = otro.id, nombre = otro.nombre, fotoUrl = otro.fotoUrl)),
                mensaje = CreateMensajeDto(usuarioId = miId, usuario = yo, texto = texto, createdAt = Instant.now().toString()),
            )
            Result.success(Unit)
        } catch (e: FirebaseFirestoreException) {
            Log.d("ComunidadRepository", "enviarMensaje: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("ComunidadRepository", "enviarMensaje: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Lo que se copia de mi en lo que escribo: mi nombre (o el usuario del correo) y mi foto
    private suspend fun miCopia(miId: String): UsuarioDto =
        UsuarioDto(
            id = miId,
            nombre = nombreDeLaSesion(
                usuarioRemoteDataSource = usuarioRemoteDataSource,
                authRemoteDataSource = authRemoteDataSource,
                usuarioId = miId,
            ),
            fotoUrl = authRemoteDataSource.currentUser?.photoUrl?.toString(),
        )
}
