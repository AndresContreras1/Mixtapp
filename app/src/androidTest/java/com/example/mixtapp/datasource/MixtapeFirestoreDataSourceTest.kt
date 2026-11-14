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
import kotlinx.coroutines.tasks.await
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
    fun agregarItem_dosVecesElMismo_noSeRepite() = runTest {
        //Arrange
        val id = dataSource.crearMixtape(generateMixtape(1))
        //Act
        dataSource.agregarItem(mixtapeId = id, itemId = "album_1", item = generateItemMixtape(1))
        dataSource.agregarItem(mixtapeId = id, itemId = "album_1", item = generateItemMixtape(1))
        //Assert: los items llegan de la subcoleccion
        val items = dataSource.listenItemsDeMixtape(id).first { it.isNotEmpty() }
        Truth.assertThat(items.map { it.id }).containsExactly("album_1")
    }

    @Test
    fun agregarItem_nuevo_subeElContadorYGuardaSuPortada() = runTest {
        //Arrange
        val id = dataSource.crearMixtape(generateMixtape(1))
        //Act
        dataSource.agregarItem(mixtapeId = id, itemId = "album_1", item = generateItemMixtape(1))
        //Assert: se lee el documento del servidor; la transaccion no pasa por la cache
        val documento = db.collection("mixtapes").document(id).get().await()
        Truth.assertThat(documento.getLong("itemsCount")).isEqualTo(1)
        Truth.assertThat(documento.get("portadas") as List<*>).containsExactly(generateItemMixtape(1).imagenUrl)
    }

    @Test
    fun agregarItem_mixtapeQueNoExiste_false() = runTest {
        //Act
        val agregado = dataSource.agregarItem(mixtapeId = "no_existe", itemId = "album_1", item = generateItemMixtape(1))
        //Assert: el repositorio decide el error
        Truth.assertThat(agregado).isFalse()
    }

    @Test
    fun quitarItem_unoDeDos_quedaElOtroYBajaElContador() = runTest {
        //Arrange
        val id = dataSource.crearMixtape(generateMixtape(1))
        dataSource.agregarItem(mixtapeId = id, itemId = "album_1", item = generateItemMixtape(1))
        dataSource.agregarItem(mixtapeId = id, itemId = "album_2", item = generateItemMixtape(2))
        //Act
        dataSource.quitarItem(mixtapeId = id, itemId = "album_1")
        //Assert
        val items = dataSource.listenItemsDeMixtape(id).first { it.size == 1 }
        Truth.assertThat(items.map { it.id }).containsExactly("album_2")
        val documento = db.collection("mixtapes").document(id).get().await()
        Truth.assertThat(documento.getLong("itemsCount")).isEqualTo(1)
    }

    @Test
    fun listenMixtapesDeUsuario_conItem_marcaEnCualesYaEsta() = runTest {
        //Arrange: el album_1 solo esta en la primera
        val conElItem = dataSource.crearMixtape(generateMixtape(1))
        val sinElItem = dataSource.crearMixtape(generateMixtape(1, createdAt = "2025-01-05T00:00:00Z"))
        dataSource.agregarItem(mixtapeId = conElItem, itemId = "album_1", item = generateItemMixtape(1))
        //Act
        val mixtapes = dataSource.listenMixtapesDeUsuario(usuarioId = "usuario_1", itemId = "album_1")
            .first { lista -> lista.size == 2 && lista.any { it.tieneElItem } }
        //Assert
        Truth.assertThat(mixtapes.first { it.id == conElItem }.tieneElItem).isTrue()
        Truth.assertThat(mixtapes.first { it.id == sinElItem }.tieneElItem).isFalse()
    }

    @Test
    fun eliminarMixtape_conItems_seBorranLaMixtapeYSusItems() = runTest {
        //Arrange
        val id = dataSource.crearMixtape(generateMixtape(1))
        dataSource.agregarItem(mixtapeId = id, itemId = "album_1", item = generateItemMixtape(1))
        //Act
        dataSource.eliminarMixtape(id)
        //Assert: borrar el documento no borra la subcoleccion; el data source la borra
        val mixtape = dataSource.listenMixtapeById(id).first { it == null }
        Truth.assertThat(mixtape).isNull()
        val items = db.collection("mixtapes").document(id).collection("items").get().await()
        Truth.assertThat(items.isEmpty).isTrue()
    }
}
