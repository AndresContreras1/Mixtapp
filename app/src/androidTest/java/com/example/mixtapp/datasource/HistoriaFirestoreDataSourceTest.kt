package com.example.mixtapp.datasource

import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.conectarEmulador
import com.example.mixtapp.data.datasource.impl.firestore.HistoriaFirestoreDataSourceImpl
import com.example.mixtapp.limpiarBase
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// El data source de los datos curiosos contra el emulador: la historia de un item por secciones,
// en el mismo orden en que se guardaron
class HistoriaFirestoreDataSourceTest {

    private val db = Firebase.firestore
    private lateinit var dataSource: HistoriaFirestoreDataSourceImpl

    @Before
    fun setUp() = runTest {
        conectarEmulador()

        dataSource = HistoriaFirestoreDataSourceImpl(db)
        limpiarBase(db)
        cargarDatosDePrueba(db)
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
    }

    @Test
    fun getHistoria_itemConHistoria_seccionesEnOrden() = runTest {
        //Act
        val historia = dataSource.getHistoria("album_1")
        //Assert
        Truth.assertThat(historia?.secciones?.map { it.titulo }).containsExactly("Historia", "Dato curioso").inOrder()
        Truth.assertThat(historia?.secciones?.first()?.texto).isEqualTo("Historia del album 1")
    }

    @Test
    fun getHistoria_itemSinHistoria_null() = runTest {
        //Act
        val historia = dataSource.getHistoria("album_999")
        //Assert
        Truth.assertThat(historia).isNull()
    }
}
