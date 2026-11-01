package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.ArtistaRemoteDataSource
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.BusquedaLocalDataSource
import com.example.mixtapp.data.datasource.CancionRemoteDataSource
import com.example.mixtapp.data.datasource.HistoriaRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.toCopiaDeItemDto
import com.example.mixtapp.data.dtos.toHistoriaUi
import com.example.mixtapp.data.dtos.toItemUi
import com.example.mixtapp.data.dtos.toReviewUi
import com.example.mixtapp.data.model.HistoriaUi
import com.example.mixtapp.data.model.ItemConResenasUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.SearchCategoryUi
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.time.Instant
import javax.inject.Inject
import kotlin.math.roundToInt

// Desde esta calificacion se cuenta que la resena recomienda el item
private const val CALIFICACION_PARA_RECOMENDAR = 4
// Cuantos lanzamientos muestra la pestana de notificaciones
private const val MAX_LANZAMIENTOS = 20

// Organiza el catalogo: albumes, canciones y artistas, cada uno con su data source, y las resenas
// de cada item. Recibe los contratos: para volver al backend de Express basta con cambiar los
// @Binds de DataSourceModule a las implementaciones de Retrofit.
// Authentication dice quien es el usuario de la app: con eso se marcan mi resena y mis likes
class CatalogoRepository @Inject constructor(
    private val busquedaLocalDataSource: BusquedaLocalDataSource,
    private val albumRemoteDataSource: AlbumRemoteDataSource,
    private val cancionRemoteDataSource: CancionRemoteDataSource,
    private val artistaRemoteDataSource: ArtistaRemoteDataSource,
    private val reviewRemoteDataSource: ReviewRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource,
    private val historiaRemoteDataSource: HistoriaRemoteDataSource,
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    // Todo el catalogo en vivo con las estadisticas de cada item, para el inicio y Buscar. Son
    // cuatro listeners: los albumes, las canciones y los artistas (un like cambia su likesCount) y
    // las resenas. Cuando llega cualquiera, combine arma la lista de nuevo. Como en clase, retorna
    // el flujo y no un Result: los errores los recibe el ViewModel en el catch
    fun getCatalogoLive(): Flow<List<ItemConResenasUi>> {
        val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

        return combine(
            albumRemoteDataSource.listenAlbumes(),
            cancionRemoteDataSource.listenCanciones(),
            artistaRemoteDataSource.listenArtistas(),
            // Con mi uid llega mi like de cada resena: las de quienes sigo se pintan con el corazon
            reviewRemoteDataSource.listenAllReviews(currentUserId = usuarioActualId),
        ) { albumes, canciones, artistas, reviews ->
            val items = albumes.map { it.toItemUi() } +
                canciones.map { it.toItemUi() } +
                artistas.map { it.toItemUi() }
            val reviewsUi = reviews.map { it.toReviewUi(usuarioActualId = usuarioActualId) }

            items.map { item ->
                conEstadisticas(item = item, reviews = reviewsUi.filter { review -> review.item.id == item.id })
            }
        }
            .catch { e ->
                Log.d("CatalogoRepository", "getCatalogoLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // El detalle en vivo con tres listeners: el item (mi like cambia su likesCount), mi guardado y
    // sus resenas. Cuando llega cualquiera, combine arma el detalle de nuevo
    fun getItemLive(tipo: String, itemId: String): Flow<ItemConResenasUi> {
        val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""
        // Sin sesion no hay guardados que escuchar
        val guardado = if (usuarioActualId.isEmpty()) flowOf(false)
        else usuarioRemoteDataSource.listenGuardado(usuarioId = usuarioActualId, itemId = itemId)

        return combine(
            listenItem(tipo = tipo, itemId = itemId, usuarioActualId = usuarioActualId),
            guardado,
            reviewRemoteDataSource.listenReviewsDeItem(tipo = tipo, itemId = itemId, currentUserId = usuarioActualId),
        ) { item, estaGuardado, reviews ->
            // El data source envia null si no existe: el repositorio decide el error
            if (item == null) throw ContenidoNoEncontradoException()

            conEstadisticas(
                item = item.copy(isSaved = estaGuardado),
                reviews = reviews.map { it.toReviewUi(usuarioActualId = usuarioActualId) },
            )
        }
            .catch { e ->
                Log.d("CatalogoRepository", "getItemLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Lanzamientos de lo que me gusta: los albumes y las canciones de los artistas que estan en mis
    // likes o mis guardados, del mas nuevo al mas viejo. Son cinco listeners: los tres del catalogo
    // (para saber de quien es cada item que me gusta) y mis dos listas. Sin sesion llega vacia
    fun getLanzamientosLive(): Flow<List<ItemUi>> {
        val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""
        if (usuarioActualId.isEmpty()) return flowOf(emptyList())

        return combine(
            albumRemoteDataSource.listenAlbumes(),
            cancionRemoteDataSource.listenCanciones(),
            artistaRemoteDataSource.listenArtistas(),
            usuarioRemoteDataSource.listenLikes(usuarioActualId),
            usuarioRemoteDataSource.listenGuardados(usuarioActualId),
        ) { albumes, canciones, artistas, likes, guardados ->
            val items = albumes.map { it.toItemUi() } +
                canciones.map { it.toItemUi() } +
                artistas.map { it.toItemUi() }
            val idsQueMeGustan = (likes + guardados).map { it.id }.toSet()
            val artistasQueMeGustan = items.filter { it.id in idsQueMeGustan }.map { it.artista }.toSet()

            // Las fechas van en formato AAAA-MM-DD, asi que ordenar el texto ordena las fechas
            items.filter { it.tipo != TIPO_ARTISTA && it.artista in artistasQueMeGustan }
                .sortedByDescending { it.fechaLanzamiento }
                .take(MAX_LANZAMIENTOS)
        }
            .catch { e ->
                Log.d("CatalogoRepository", "getLanzamientosLive: ${e.message}")
                throw errorDelFlujo(e)
            }
    }

    // Los datos curiosos por secciones, una sola vez: no cambian mientras se usa la app
    suspend fun getHistoria(itemId: String): Result<HistoriaUi> {
        return try {
            val historia = historiaRemoteDataSource.getHistoria(itemId)
            // El data source retorna null si no existe o si no tiene secciones: el repositorio
            // decide el error
            val historiaUi = historia?.toHistoriaUi()
            if (historiaUi == null || historiaUi.secciones.isEmpty()) {
                return Result.failure(ContenidoNoEncontradoException())
            }

            Result.success(historiaUi)
        } catch (e: FirebaseFirestoreException) {
            Log.d("CatalogoRepository", "getHistoria: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("CatalogoRepository", "getHistoria: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Escribir resena: el item y sus resenas una sola vez, para saber si ya lo califique
    suspend fun getItemConResenas(tipo: String, itemId: String): Result<ItemConResenasUi> {
        return try {
            val usuarioActualId = authRemoteDataSource.currentUser?.uid ?: ""

            // El item y sus resenas salen a la vez. Si el item no existe queda null
            val itemConResenas = coroutineScope {
                val item = async { getItem(tipo = tipo, itemId = itemId, usuarioActualId = usuarioActualId) }
                // Escribir resena solo busca la mia, sin likes
                val reviews = async { reviewRemoteDataSource.getReviewsDeItem(tipo = tipo, itemId = itemId) }
                val itemUi = item.await() ?: return@coroutineScope null

                conEstadisticas(
                    item = itemUi,
                    reviews = reviews.await().map { it.toReviewUi(usuarioActualId = usuarioActualId) },
                )
            }

            // El data source retorna null si no existe: el repositorio es el que decide el error
            if (itemConResenas == null) return Result.failure(ContenidoNoEncontradoException())

            Result.success(itemConResenas)
        } catch (e: HttpException) {
            Log.d("CatalogoRepository", "getItemConResenas: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("CatalogoRepository", "getItemConResenas: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("CatalogoRepository", "getItemConResenas: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Da el like o lo quita: el data source decide segun si ya existe. El corazon y el contador
    // cambian cuando llega el item nuevo por el listener. La copia del item queda en mis likes, de
    // donde salen mis favoritos y el For you
    suspend fun sendOrDeleteLike(item: ItemUi): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())
            val copia = item.toCopiaDeItemDto(createdAt = Instant.now().toString())

            when (item.tipo) {
                TIPO_CANCION -> cancionRemoteDataSource.sendOrDeleteLike(cancionId = item.id, userId = usuarioId, copia = copia)
                TIPO_ARTISTA -> artistaRemoteDataSource.sendOrDeleteLike(artistaId = item.id, userId = usuarioId, copia = copia)
                else -> albumRemoteDataSource.sendOrDeleteLike(albumId = item.id, userId = usuarioId, copia = copia)
            }
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("CatalogoRepository", "sendOrDeleteLike: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("CatalogoRepository", "sendOrDeleteLike: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("CatalogoRepository", "sendOrDeleteLike: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Guarda el item en mi perfil o lo quita: el boton cambia cuando llega mi guardado por el listener
    suspend fun sendOrDeleteGuardado(item: ItemUi): Result<Unit> {
        return try {
            val usuarioId = authRemoteDataSource.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            usuarioRemoteDataSource.sendOrDeleteGuardado(
                usuarioId = usuarioId,
                itemId = item.id,
                copia = item.toCopiaDeItemDto(createdAt = Instant.now().toString()),
            )
            Result.success(Unit)
        } catch (e: HttpException) {
            Log.d("CatalogoRepository", "sendOrDeleteGuardado: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: FirebaseFirestoreException) {
            Log.d("CatalogoRepository", "sendOrDeleteGuardado: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: Exception) {
            Log.d("CatalogoRepository", "sendOrDeleteGuardado: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    suspend fun getSearchCategories(): Result<List<SearchCategoryUi>> {
        return try {
            Result.success(busquedaLocalDataSource.getSearchCategories())
        } catch (e: Exception) {
            Log.d("CatalogoRepository", "getSearchCategories: ${e.message}")
            Result.failure(ErrorDeDatosLocalesException())
        }
    }

    // Cada tipo tiene su data source: el detalle escucha el del tipo que llega
    private fun listenItem(tipo: String, itemId: String, usuarioActualId: String): Flow<ItemUi?> =
        when (tipo) {
            TIPO_CANCION -> cancionRemoteDataSource.listenCancionById(itemId, currentUserId = usuarioActualId)
                .map { cancion -> cancion?.toItemUi() }
            TIPO_ARTISTA -> artistaRemoteDataSource.listenArtistaById(itemId, currentUserId = usuarioActualId)
                .map { artista -> artista?.toItemUi() }
            else -> albumRemoteDataSource.listenAlbumById(itemId, currentUserId = usuarioActualId)
                .map { album -> album?.toItemUi() }
        }

    private suspend fun getItem(tipo: String, itemId: String, usuarioActualId: String): ItemUi? =
        when (tipo) {
            TIPO_CANCION -> cancionRemoteDataSource.getCancionById(itemId, currentUserId = usuarioActualId)?.toItemUi()
            TIPO_ARTISTA -> artistaRemoteDataSource.getArtistaById(itemId, currentUserId = usuarioActualId)?.toItemUi()
            else -> albumRemoteDataSource.getAlbumById(itemId, currentUserId = usuarioActualId)?.toItemUi()
        }

    // El promedio, el total, el porcentaje que recomienda y mi calificacion salen de las resenas.
    // Las resenas quedan de la ultima publicada o editada a la mas vieja
    private fun conEstadisticas(item: ItemUi, reviews: List<ReviewUi>): ItemConResenasUi {
        var suma = 0
        var recomiendan = 0

        reviews.forEach { review ->
            suma += review.rating
            if (review.rating >= CALIFICACION_PARA_RECOMENDAR) recomiendan++
        }

        val promedio = if (reviews.isEmpty()) 0.0 else suma.toDouble() / reviews.size
        val porcentaje = if (reviews.isEmpty()) 0 else recomiendan * 100 / reviews.size
        val miResena = reviews.find { it.isMine }

        return ItemConResenasUi(
            item = item,
            rating = promedio,
            estrellas = promedio.roundToInt(),
            ratingCount = reviews.size,
            recommendRate = porcentaje,
            userRating = miResena?.rating ?: 0,
            // Las fechas van en formato ISO, asi que ordenar el texto ordena las fechas
            reviews = reviews.sortedByDescending { it.updatedAt },
        )
    }
}
