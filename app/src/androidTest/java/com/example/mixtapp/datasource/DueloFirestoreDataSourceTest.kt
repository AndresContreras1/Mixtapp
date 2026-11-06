package com.example.mixtapp.datasource

import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.conectarEmulador
import com.example.mixtapp.data.datasource.impl.firestore.DueloFirestoreDataSourceImpl
import com.example.mixtapp.data.model.OPCION_A
import com.example.mixtapp.data.model.OPCION_B
import com.example.mixtapp.generateDuelo
import com.example.mixtapp.generateVoto
import com.example.mixtapp.limpiarBase
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// El data source de los duelos contra el emulador: la transaccion del voto (crear el duelo, sumar,
// cambiar de opinion) y las opiniones en vivo
class DueloFirestoreDataSourceTest {

    private val db = Firebase.firestore
    private lateinit var dataSource: DueloFirestoreDataSourceImpl

    private val dueloId = "dia_1_album"

    @Before
    fun setUp() = runTest {
        conectarEmulador()

        dataSource = DueloFirestoreDataSourceImpl(db)
        limpiarBase(db)
        cargarDatosDePrueba(db)
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
    }

    @Test
    fun listenDuelo_nadieHaVotado_null() = runTest {
        //Act
        val duelo = dataSource.listenDuelo(dueloId).first()
        //Assert
        Truth.assertThat(duelo).isNull()
    }

    @Test
    fun votar_primerVoto_creaElDueloConEseVoto() = runTest {
        //Act
        dataSource.votar(dueloId, generateDuelo(votosA = 1, votosB = 0), usuarioId = "usuario_1", voto = generateVoto(1, OPCION_A))
        //Assert
        val duelo = dataSource.listenDuelo(dueloId).first { it != null }
        Truth.assertThat(duelo?.votosA).isEqualTo(1)
        Truth.assertThat(duelo?.votosB).isEqualTo(0)
        Truth.assertThat(duelo?.itemA?.id).isEqualTo("album_1")
    }

    @Test
    fun votar_otroUsuario_sumaSinCrearOtroDuelo() = runTest {
        //Arrange
        dataSource.votar(dueloId, generateDuelo(votosA = 1, votosB = 0), usuarioId = "usuario_1", voto = generateVoto(1, OPCION_A))
        //Act: el duelo que manda el segundo no se usa, porque ya existe
        dataSource.votar(dueloId, generateDuelo(votosA = 0, votosB = 1), usuarioId = "usuario_2", voto = generateVoto(2, OPCION_B))
        //Assert
        val duelo = dataSource.listenDuelo(dueloId).first { it?.votosB == 1 }
        Truth.assertThat(duelo?.votosA).isEqualTo(1)
    }

    @Test
    fun votar_cambiaDeOpcion_elVotoPasaAlOtro() = runTest {
        //Arrange
        dataSource.votar(dueloId, generateDuelo(votosA = 1, votosB = 0), usuarioId = "usuario_1", voto = generateVoto(1, OPCION_A))
        //Act
        dataSource.votar(dueloId, generateDuelo(votosA = 0, votosB = 1), usuarioId = "usuario_1", voto = generateVoto(1, OPCION_B))
        //Assert
        val duelo = dataSource.listenDuelo(dueloId).first { it?.votosB == 1 }
        Truth.assertThat(duelo?.votosA).isEqualTo(0)
    }

    @Test
    fun votar_mismaOpcionDosVeces_noSumaDosVeces() = runTest {
        //Arrange
        dataSource.votar(dueloId, generateDuelo(votosA = 1, votosB = 0), usuarioId = "usuario_1", voto = generateVoto(1, OPCION_A))
        //Act: solo cambia la opinion
        dataSource.votar(dueloId, generateDuelo(votosA = 1, votosB = 0), usuarioId = "usuario_1", voto = generateVoto(1, OPCION_A))
        //Assert
        val duelo = dataSource.listenDuelo(dueloId).first { it != null }
        Truth.assertThat(duelo?.votosA).isEqualTo(1)
    }

    @Test
    fun listenVotos_dosVotos_elMasNuevoPrimeroConElUidComoId() = runTest {
        //Arrange
        dataSource.votar(dueloId, generateDuelo(votosA = 1, votosB = 0), usuarioId = "usuario_1", voto = generateVoto(1, OPCION_A))
        dataSource.votar(dueloId, generateDuelo(votosA = 0, votosB = 1), usuarioId = "usuario_2", voto = generateVoto(2, OPCION_B))
        //Act
        val votos = dataSource.listenVotos(dueloId).first { it.size == 2 }
        //Assert
        Truth.assertThat(votos.map { it.id }).containsExactly("usuario_2", "usuario_1").inOrder()
        Truth.assertThat(votos[0].usuario?.nombre).isEqualTo("Nombre 2")
    }
}
