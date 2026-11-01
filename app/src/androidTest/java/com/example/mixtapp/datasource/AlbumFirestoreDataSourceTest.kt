package com.example.mixtapp.datasource

import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.conectarEmulador
import com.example.mixtapp.data.datasource.impl.firestore.AlbumFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.toCopiaDeItemDto
import com.example.mixtapp.data.dtos.toItemUi
import com.example.mixtapp.generateAlbum
import com.example.mixtapp.limpiarBase
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// El data source de albumes contra el emulador: leer, y el like a un item, que en una transaccion
// sube el contador y deja la copia del album en los likes del usuario
class AlbumFirestoreDataSourceTest {

    private val db = Firebase.firestore
    private lateinit var dataSource: AlbumFirestoreDataSourceImpl

    private val copia = generateAlbum(1).toItemUi().toCopiaDeItemDto(createdAt = "2026-10-31T00:00:00Z")

    @Before
    fun setUp() = runTest {
        conectarEmulador()

        dataSource = AlbumFirestoreDataSourceImpl(db)
        limpiarBase(db)
        cargarDatosDePrueba(db)
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
    }

    @Test
    fun getAlbumById_validId_correctAlbum() = runTest {
        //Act
        val album = dataSource.getAlbumById("album_1")
        //Assert
        Truth.assertThat(album?.titulo).isEqualTo("Album 1")
        Truth.assertThat(album?.subgenero).isEqualTo("Rock alternativo")
        Truth.assertThat(album?.datosCuriosos).containsExactly("Dato curioso del album 1")
    }

    @Test
    fun getAlbumById_invalidId_null() = runTest {
        //Act
        val album = dataSource.getAlbumById("album_999")
        //Assert
        Truth.assertThat(album).isNull()
    }

    @Test
    fun sendOrDeleteLike_primeraVez_subeElContadorYCopiaElAlbum() = runTest {
        //Act
        dataSource.sendOrDeleteLike(albumId = "album_1", userId = "usuario_2", copia = copia)
        //Assert
        val album = dataSource.getAlbumById("album_1", currentUserId = "usuario_2")
        Truth.assertThat(album?.likesCount).isEqualTo(1)
        Truth.assertThat(album?.liked).isTrue()
        val miLike = db.collection("usuarios").document("usuario_2")
            .collection("likes").document("album_1").get().await()
        Truth.assertThat(miLike.getString("titulo")).isEqualTo("Album 1")
        Truth.assertThat(miLike.getString("genero")).isEqualTo("Rock")
    }

    @Test
    fun sendOrDeleteLike_segundaVez_quitaElLikeYLaCopia() = runTest {
        //Arrange
        dataSource.sendOrDeleteLike(albumId = "album_1", userId = "usuario_2", copia = copia)
        //Act
        dataSource.sendOrDeleteLike(albumId = "album_1", userId = "usuario_2", copia = copia)
        //Assert
        val album = dataSource.getAlbumById("album_1", currentUserId = "usuario_2")
        Truth.assertThat(album?.likesCount).isEqualTo(0)
        Truth.assertThat(album?.liked).isFalse()
        val miLike = db.collection("usuarios").document("usuario_2")
            .collection("likes").document("album_1").get().await()
        Truth.assertThat(miLike.exists()).isFalse()
    }

    @Test
    fun listenAlbumById_conMiLike_llegaMarcado() = runTest {
        //Arrange
        dataSource.sendOrDeleteLike(albumId = "album_1", userId = "usuario_2", copia = copia)
        //Act: el listener puede emitir primero la cache local; se espera el album con el contador
        val album = dataSource.listenAlbumById("album_1", currentUserId = "usuario_2")
            .first { it?.likesCount == 1 }
        //Assert
        Truth.assertThat(album?.liked).isTrue()
    }
}
