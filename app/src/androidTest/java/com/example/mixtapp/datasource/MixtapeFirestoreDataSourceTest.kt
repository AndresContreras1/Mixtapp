package com.example.mixtapp.datasource

import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.conectarEmulador
import com.example.mixtapp.data.datasource.impl.firestore.MixtapeFirestoreDataSourceImpl
import com.example.mixtapp.generateItemMixtape
import com.example.mixtapp.generateMixtape
import com.example.mixtapp.limpiarBase
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// El data source de las mixtapes contra el emulador: crearlas, agregar y quitar items sin repetir
// y borrarlas
class MixtapeFirestoreDataSourceTest {

    private val db = Firebase.firestore
    private lateinit var dataSource: MixtapeFirestoreDataSourceImpl

    @Before
    fun setUp() = runTest {
        conectarEmulador()

        dataSource = MixtapeFirestoreDataSourceImpl(db)
        limpiarBase(db)
        cargarDatosDePrueba(db)
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
    }

    @Test
    fun crearMixtape_nueva_apareceEnLasDelUsuario() = runTest {
        //Act
        val id = dataSource.crearMixtape(generateMixtape(1))
        //Assert
        val mixtapes = dataSource.listenMixtapesDeUsuario("usuario_1").first { it.isNotEmpty() }
        Truth.assertThat(mixtapes.map { it.id }).containsExactly(id)
    }

    @Test
    fun listenMixtapesDeUsuario_dosMixtapes_laMasNuevaPrimero() = runTest {
        //Arrange
        val vieja = dataSource.crearMixtape(generateMixtape(1, createdAt = "2025-01-01T00:00:00Z"))
        val nueva = dataSource.crearMixtape(generateMixtape(1, createdAt = "2025-01-02T00:00:00Z"))
        //Act
        val mixtapes = dataSource.listenMixtapesDeUsuario("usuario_1").first { it.size == 2 }
        //Assert
        Truth.assertThat(mixtapes.map { it.id }).containsExactly(nueva, vieja).inOrder()
    }

    @Test
    fun agregarItem_dosVecesElMismo_noSeRepite() = runTest {
        //Arrange
        val id = dataSource.crearMixtape(generateMixtape(1))
        //Act
        dataSource.agregarItem(mixtapeId = id, item = generateItemMixtape(1))
        dataSource.agregarItem(mixtapeId = id, item = generateItemMixtape(1))
        //Assert
        val mixtape = dataSource.listenMixtapeById(id).first { it?.items?.isNotEmpty() == true }
        Truth.assertThat(mixtape?.items?.map { it.id }).containsExactly("album_1")
    }

    @Test
    fun quitarItem_unoDeDos_quedaElOtro() = runTest {
        //Arrange
        val id = dataSource.crearMixtape(generateMixtape(1))
        dataSource.agregarItem(mixtapeId = id, item = generateItemMixtape(1))
        dataSource.agregarItem(mixtapeId = id, item = generateItemMixtape(2))
        //Act
        dataSource.quitarItem(mixtapeId = id, itemId = "album_1")
        //Assert
        val mixtape = dataSource.listenMixtapeById(id).first { it?.items?.size == 1 }
        Truth.assertThat(mixtape?.items?.map { it.id }).containsExactly("album_2")
    }

    @Test
    fun eliminarMixtape_existente_llegaNull() = runTest {
        //Arrange
        val id = dataSource.crearMixtape(generateMixtape(1))
        //Act
        dataSource.eliminarMixtape(id)
        //Assert
        val mixtape = dataSource.listenMixtapeById(id).first { it == null }
        Truth.assertThat(mixtape).isNull()
    }
}
