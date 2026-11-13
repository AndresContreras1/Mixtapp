package com.example.mixtapp.datasource

import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.conectarEmulador
import com.example.mixtapp.data.datasource.impl.firestore.SonandoFirestoreDataSourceImpl
import com.example.mixtapp.generateReaccion
import com.example.mixtapp.generateSonando
import com.example.mixtapp.limpiarBase
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// El data source de "sonando ahora" contra el emulador: poner y quitar lo que suena, la reaccion
// 🔥 con su contador y la lista en vivo
class SonandoFirestoreDataSourceTest {

    private val db = Firebase.firestore
    private lateinit var dataSource: SonandoFirestoreDataSourceImpl

    @Before
    fun setUp() = runTest {
        conectarEmulador()

        dataSource = SonandoFirestoreDataSourceImpl(db)
        limpiarBase(db)
        cargarDatosDePrueba(db)
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
    }

    @Test
    fun ponerSonando_usuarioNuevo_apareceEnLaLista() = runTest {
        //Act
        dataSource.ponerSonando(usuarioId = "usuario_1", sonando = generateSonando(1))
        //Assert: el id del documento es el uid
        val lista = dataSource.listenSonando(currentUserId = "usuario_2").first { it.isNotEmpty() }
        Truth.assertThat(lista.map { it.id }).containsExactly("usuario_1")
        Truth.assertThat(lista[0].titulo).isEqualTo("Album 1")
    }

    @Test
    fun listenSonando_dosUsuarios_elMasRecientePrimero() = runTest {
        //Arrange
        dataSource.ponerSonando(usuarioId = "usuario_1", sonando = generateSonando(1))
        dataSource.ponerSonando(usuarioId = "usuario_2", sonando = generateSonando(2))
        //Act
        val lista = dataSource.listenSonando(currentUserId = "usuario_3").first { it.size == 2 }
        //Assert
        Truth.assertThat(lista.map { it.id }).containsExactly("usuario_2", "usuario_1").inOrder()
    }

    @Test
    fun reaccionar_primeraVez_subeElContador() = runTest {
        //Arrange
        dataSource.ponerSonando(usuarioId = "usuario_1", sonando = generateSonando(1))
        //Act
        dataSource.reaccionar(usuarioId = "usuario_1", reaccion = generateReaccion(2))
        //Assert
        val lista = dataSource.listenSonando(currentUserId = "usuario_2").first { it.firstOrNull()?.reaccionesCount == 1 }
        Truth.assertThat(lista[0].reaccionesCount).isEqualTo(1)
    }

    @Test
    fun reaccionar_primeraVez_quedaReaccionado() = runTest {
        //Arrange
        dataSource.ponerSonando(usuarioId = "usuario_1", sonando = generateSonando(1))
        //Act
        dataSource.reaccionar(usuarioId = "usuario_1", reaccion = generateReaccion(2))
        //Assert: con mi uid se marca que ya reaccione
        val lista = dataSource.listenSonando(currentUserId = "usuario_2").first { it.firstOrNull()?.reaccionado == true }
        Truth.assertThat(lista[0].reaccionado).isTrue()
    }

    @Test
    fun reaccionar_segundaVez_quitaLaReaccion() = runTest {
        //Arrange
        dataSource.ponerSonando(usuarioId = "usuario_1", sonando = generateSonando(1))
        dataSource.reaccionar(usuarioId = "usuario_1", reaccion = generateReaccion(2))
        //Act
        dataSource.reaccionar(usuarioId = "usuario_1", reaccion = generateReaccion(2))
        //Assert
        val lista = dataSource.listenSonando(currentUserId = "usuario_2").first { it.firstOrNull()?.reaccionesCount == 0 }
        Truth.assertThat(lista[0].reaccionado).isFalse()
    }

    @Test
    fun ponerSonando_otraVez_reiniciaLasReacciones() = runTest {
        //Arrange
        dataSource.ponerSonando(usuarioId = "usuario_1", sonando = generateSonando(1))
        dataSource.reaccionar(usuarioId = "usuario_1", reaccion = generateReaccion(2))
        //Act: vuelve a poner el mismo album
        dataSource.ponerSonando(usuarioId = "usuario_1", sonando = generateSonando(1))
        //Assert: la reaccion vieja ya no cuenta
        val lista = dataSource.listenSonando(currentUserId = "usuario_2").first { it.firstOrNull()?.reaccionesCount == 0 }
        Truth.assertThat(lista[0].reaccionado).isFalse()
    }

    @Test
    fun quitarSonando_conReacciones_desapareceDeLaLista() = runTest {
        //Arrange
        dataSource.ponerSonando(usuarioId = "usuario_1", sonando = generateSonando(1))
        dataSource.reaccionar(usuarioId = "usuario_1", reaccion = generateReaccion(2))
        //Act
        dataSource.quitarSonando(usuarioId = "usuario_1")
        //Assert
        val lista = dataSource.listenSonando(currentUserId = "usuario_2").first { it.isEmpty() }
        Truth.assertThat(lista).isEmpty()
    }
}
