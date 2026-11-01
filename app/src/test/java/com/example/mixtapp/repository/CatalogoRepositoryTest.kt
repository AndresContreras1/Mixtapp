package com.example.mixtapp.repository

import com.example.mixtapp.data.datasource.AlbumRemoteDataSource
import com.example.mixtapp.data.datasource.ArtistaRemoteDataSource
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.BusquedaLocalDataSource
import com.example.mixtapp.data.datasource.CancionRemoteDataSource
import com.example.mixtapp.data.datasource.ReviewRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.example.mixtapp.data.dtos.AlbumDto
import com.example.mixtapp.data.dtos.CancionDto
import com.example.mixtapp.data.dtos.ReviewDto
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.TIPO_ALBUM
import com.example.mixtapp.data.model.TIPO_ARTISTA
import com.example.mixtapp.data.model.TIPO_CANCION
import com.example.mixtapp.data.repository.CatalogoRepository
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.ErrorInesperadoException
import com.example.mixtapp.data.repository.SinSesionException
import com.google.common.truth.Truth
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

// El repositorio del catalogo con mocks: los data sources responden lo que dice cada prueba y se
// revisa solo lo que hace el repositorio (el mapeo, las estadisticas y los errores)
class CatalogoRepositoryTest {

    private val busquedaLocalDataSource = mockk<BusquedaLocalDataSource>()
    private val albumDataSource = mockk<AlbumRemoteDataSource>()
    private val cancionDataSource = mockk<CancionRemoteDataSource>()
    private val artistaDataSource = mockk<ArtistaRemoteDataSource>()
    private val reviewDataSource = mockk<ReviewRemoteDataSource>()
    private val usuarioDataSource = mockk<UsuarioRemoteDataSource>()
    private val authDataSource = mockk<AuthRemoteDataSource>()

    private val repository = CatalogoRepository(
        busquedaLocalDataSource,
        albumDataSource,
        cancionDataSource,
        artistaDataSource,
        reviewDataSource,
        usuarioDataSource,
        authDataSource,
    )

    private val fromZero = AlbumDto().copy(id = "album_1", titulo = "From Zero", artista = "Linkin Park", anio = 2024)

    @Test
    fun getItemConResenas_albumConDosResenas_calculaLasEstadisticas() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { albumDataSource.getAlbumById("album_1", "yo") } returns fromZero
        coEvery { reviewDataSource.getReviewsDeItem(TIPO_ALBUM, "album_1", any()) } returns listOf(
            ReviewDto().copy(id = "r1", calificacion = 5, usuarioId = "yo", itemId = "album_1"),
            ReviewDto().copy(id = "r2", calificacion = 3, usuarioId = "otro", itemId = "album_1"),
        )
        //Act
        val result = repository.getItemConResenas(TIPO_ALBUM, "album_1")
        //Assert
        val itemConResenas = result.getOrNull()
        Truth.assertThat(result.isSuccess).isTrue()
        Truth.assertThat(itemConResenas?.rating).isEqualTo(4.0)
        Truth.assertThat(itemConResenas?.ratingCount).isEqualTo(2)
        Truth.assertThat(itemConResenas?.recommendRate).isEqualTo(50)
        Truth.assertThat(itemConResenas?.userRating).isEqualTo(5)
        Truth.assertThat(itemConResenas?.item?.subtitulo).isEqualTo("Linkin Park")
    }

    @Test
    fun getItemConResenas_cancion_mapeaTituloAlbumYArtista() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { cancionDataSource.getCancionById("cancion_1", "yo") } returns CancionDto().copy(
            id = "cancion_1",
            titulo = "The Emptiness Machine",
            album = "From Zero",
            artista = "Linkin Park",
            likesCount = 1_240,
        )
        coEvery { reviewDataSource.getReviewsDeItem(TIPO_CANCION, "cancion_1", any()) } returns emptyList()
        //Act
        val item = repository.getItemConResenas(TIPO_CANCION, "cancion_1").getOrNull()?.item
        //Assert
        Truth.assertThat(item?.tipo).isEqualTo(TIPO_CANCION)
        Truth.assertThat(item?.subtitulo).isEqualTo("From Zero")
        Truth.assertThat(item?.detalle).isEqualTo("Linkin Park")
        Truth.assertThat(item?.likesText).isEqualTo("1.2K")
    }

    @Test
    fun getItemConResenas_artistaQueNoExiste_contenidoNoEncontrado() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { artistaDataSource.getArtistaById("artista_999", "yo") } returns null
        coEvery { reviewDataSource.getReviewsDeItem(TIPO_ARTISTA, "artista_999", any()) } returns emptyList()
        //Act
        val result = repository.getItemConResenas(TIPO_ARTISTA, "artista_999")
        //Assert
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(ContenidoNoEncontradoException::class.java)
    }

    @Test
    fun getItemConResenas_dataSourceFalla_errorInesperado() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { albumDataSource.getAlbumById(any(), any()) } throws RuntimeException("Fallo la lectura")
        coEvery { reviewDataSource.getReviewsDeItem(any(), any(), any()) } returns emptyList()
        //Act
        val result = repository.getItemConResenas(TIPO_ALBUM, "album_1")
        //Assert
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(ErrorInesperadoException::class.java)
    }

    @Test
    fun sendOrDeleteLike_sinSesion_sinSesionException() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser } returns null
        //Act
        val result = repository.sendOrDeleteLike(LocalPreviewProvider.fromZero)
        //Assert
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }

    @Test
    fun getCatalogoLive_resenasDeVariosAlbumes_cadaAlbumConLasSuyas() = runTest {
        //Arrange
        coEvery { authDataSource.currentUser?.uid } returns "yo"
        coEvery { albumDataSource.listenAlbumes() } returns flowOf(listOf(fromZero, fromZero.copy(id = "album_2")))
        coEvery { cancionDataSource.listenCanciones() } returns flowOf(emptyList())
        coEvery { artistaDataSource.listenArtistas() } returns flowOf(emptyList())
        coEvery { reviewDataSource.listenAllReviews(any()) } returns flowOf(
            listOf(
                ReviewDto().copy(id = "r1", calificacion = 4, itemId = "album_1"),
                ReviewDto().copy(id = "r2", calificacion = 2, itemId = "album_1"),
                ReviewDto().copy(id = "r3", calificacion = 5, itemId = "album_2"),
            )
        )
        //Act
        val catalogo = repository.getCatalogoLive().first()
        //Assert
        Truth.assertThat(catalogo.map { it.item.id }).containsExactly("album_1", "album_2").inOrder()
        Truth.assertThat(catalogo[0].ratingCount).isEqualTo(2)
        Truth.assertThat(catalogo[0].rating).isEqualTo(3.0)
        Truth.assertThat(catalogo[1].ratingCount).isEqualTo(1)
    }
}
