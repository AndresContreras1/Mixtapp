package com.example.mixtapp.repository

import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.MessagingRemoteDataSource
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.SeguirseASiMismoException
import com.example.mixtapp.data.repository.SinSesionException
import com.example.mixtapp.data.repository.UsuarioRepository
import com.example.mixtapp.generateUsuario
import com.example.mixtapp.limpiarBase
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// El repositorio de usuarios por integracion: el perfil se mapea a UsuarioUi y los errores llegan
// como Result. La sesion es una cuenta del Authentication del emulador
class UsuarioRepositoryIntegrationTest {

    private val db = Firebase.firestore
    private val auth = Firebase.auth

    private lateinit var usuarioRepository: UsuarioRepository
    private lateinit var dataSource: UsuarioFirestoreDataSourceImpl
    private lateinit var correo: String

    @Before
    fun setUp() = runTest {
        try {
            db.useEmulator("10.0.2.2", 8080)
        } catch (e: Exception) {

        }
        try {
            auth.useEmulator("10.0.2.2", 9099)
        } catch (e: Exception) {

        }

        dataSource = UsuarioFirestoreDataSourceImpl(db)
        usuarioRepository = UsuarioRepository(
            dataSource,
            AuthRemoteDataSource(auth),
            MessagingRemoteDataSource(FirebaseMessaging.getInstance()),
        )
        limpiarBase(db)
        cargarDatosDePrueba(db)

        // Crear la cuenta tambien inicia la sesion. Todavia no tiene documento en usuarios
        correo = "prueba${System.currentTimeMillis()}@example.com"
        auth.createUserWithEmailAndPassword(correo, "123456").await()
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
        // Si la prueba cerro la sesion se vuelve a entrar con la cuenta de prueba para poder
        // borrarla: el Authentication del emulador queda como estaba
        val user = auth.currentUser ?: auth.signInWithEmailAndPassword(correo, "123456").await().user
        user?.delete()?.await()
        auth.signOut()
    }

    // Guarda el documento de la cuenta de la sesion, con el uid como id
    private suspend fun crearMiDocumento() {
        val usuarioId = auth.currentUser?.uid ?: ""
        db.collection("usuarios").document(usuarioId).set(generateUsuario(1).copy(id = usuarioId)).await()
    }

    @Test
    fun getMiPerfil_conDocumento_esMiPerfil() = runTest {
        //Arrange
        crearMiDocumento()
        //Act
        val result = usuarioRepository.getMiPerfil()
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
        Truth.assertThat(result.getOrNull()?.nombre).isEqualTo("Nombre 1")
        Truth.assertThat(result.getOrNull()?.esMiPerfil).isTrue()
    }

    @Test
    fun getMiPerfil_sinDocumento_contenidoNoEncontrado() = runTest {
        //Act
        val result = usuarioRepository.getMiPerfil()
        //Assert
        Truth.assertThat(result.isFailure).isTrue()
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(ContenidoNoEncontradoException::class.java)
    }

    @Test
    fun getMiPerfil_sinSesion_sinSesionException() = runTest {
        //Arrange
        auth.signOut()
        //Act
        val result = usuarioRepository.getMiPerfil()
        //Assert
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(SinSesionException::class.java)
    }

    @Test
    fun registrarUsuario_validData_documentCreated() = runTest {
        //Arrange
        val usuarioId = auth.currentUser?.uid ?: ""
        //Act
        val result = usuarioRepository.registrarUsuario("prueba", "Prueba", "")
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
        Truth.assertThat(dataSource.getUsuarioById(usuarioId)?.nombre).isEqualTo("Prueba")
    }

    @Test
    fun followOrUnfollow_mismoUsuario_seguirseASiMismoException() = runTest {
        //Arrange
        val usuarioId = auth.currentUser?.uid ?: ""
        //Act
        val result = usuarioRepository.followOrUnfollow(usuarioId)
        //Assert
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(SeguirseASiMismoException::class.java)
    }

    @Test
    fun followOrUnfollow_sinMiDocumento_creaMiDocumentoYSigue() = runTest {
        //Arrange: la cuenta de la sesion no tiene documento en usuarios, como pasa en modo debug
        // cuando el emulador se reinicia y la sesion sigue guardada en el celular
        val miId = auth.currentUser?.uid ?: ""
        //Act
        val result = usuarioRepository.followOrUnfollow(targetUserId = "usuario_2")
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
        Truth.assertThat(dataSource.getUsuarioById(miId)).isNotNull()
        Truth.assertThat(dataSource.getUsuarioById("usuario_2", currentUserId = miId)?.followed).isTrue()
    }

    @Test
    fun followOrUnfollow_usuarioQueNoExiste_contenidoNoEncontradoException() = runTest {
        //Arrange
        crearMiDocumento()
        //Act
        val result = usuarioRepository.followOrUnfollow(targetUserId = "usuario_que_no_existe")
        //Assert
        Truth.assertThat(result.exceptionOrNull()).isInstanceOf(ContenidoNoEncontradoException::class.java)
    }

    @Test
    fun followOrUnfollow_otroUsuario_success() = runTest {
        //Arrange
        crearMiDocumento()
        //Act
        val result = usuarioRepository.followOrUnfollow("usuario_2")
        //Assert
        Truth.assertThat(result.isSuccess).isTrue()
    }

    @Test
    fun actualizarPerfil_conDocumento_nombreActualizado() = runTest {
        //Arrange
        crearMiDocumento()
        //Act
        usuarioRepository.actualizarPerfil("Nuevo nombre", "nuevo", "")
        //Assert
        val result = usuarioRepository.getMiPerfil()
        Truth.assertThat(result.getOrNull()?.nombre).isEqualTo("Nuevo nombre")
    }
}
