package com.example.mixtapp.datasource

import com.example.mixtapp.cargarDatosDePrueba
import com.example.mixtapp.conectarEmulador
import com.example.mixtapp.data.datasource.impl.firestore.UsuarioFirestoreDataSourceImpl
import com.example.mixtapp.data.dtos.RegistrarUsuarioDto
import com.example.mixtapp.generateUsuario
import com.example.mixtapp.limpiarBase
import com.google.common.truth.Truth
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

// Pruebas de integracion contra el emulador de Firebase: necesitan el emulador de Android prendido
// y la Firebase CLI corriendo (firebase emulators:start --only firestore,auth)
class UsuarioFirestoreDataSourceTest {

    private val db = Firebase.firestore

    private lateinit var dataSource: UsuarioFirestoreDataSourceImpl

    @Before
    fun setUp() = runTest {
        // En debug la app ya conecto esta instancia al emulador: conectarla otra vez lanza
        // IllegalStateException, por eso va en un try
        conectarEmulador()

        dataSource = UsuarioFirestoreDataSourceImpl(db)
        limpiarBase(db)
        cargarDatosDePrueba(db)
    }

    @After
    fun tearDown() = runTest {
        limpiarBase(db)
    }

    @Test
    fun getUsuarioById_validId_correctUser() = runTest {
        //Arrange
        val id = "usuario_9"
        val expectedName = "Nombre 9"
        //Act
        val result = dataSource.getUsuarioById(id, "")
        //Assert
        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result?.nombre).isEqualTo(expectedName)
        Truth.assertThat(result?.id).isEqualTo(id)
    }

    @Test
    fun getUsuarioById_invalidId_null() = runTest {
        //Arrange
        val id = "usuario_999"
        //Act
        val result = dataSource.getUsuarioById(id, "")
        //Assert
        Truth.assertThat(result).isNull()
    }

    @Test
    fun registrarUsuario_insertDocument_documentExists() = runTest {
        //Arrange
        val usuario = RegistrarUsuarioDto(
            username = "username",
            nombre = "nombre",
            bio = "bio",
            fotoUrl = null,
            FCMToken = "",
        )
        //Act
        dataSource.registrarUsuario(usuario, "999")
        //Assert
        val result = dataSource.getUsuarioById("999", "")
        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result?.nombre).isEqualTo("nombre")
        Truth.assertThat(result?.id).isEqualTo("999")
    }

    @Test
    fun followOrUnfollow_followUser_userFollowed() = runTest {
        //Arrange
        val currentUser = generateUsuario(1)
        val targetUser = generateUsuario(2)
        //Act
        dataSource.followOrUnfollow(currentUser.id, targetUser.id)
        //Assert
        val targetUserResult = dataSource.getUsuarioById(targetUser.id, currentUser.id)
        Truth.assertThat(targetUserResult?.followed).isTrue()
    }

    @Test
    fun followOrUnfollow_followUser_followersCountIncrease() = runTest {
        //Arrange
        val currentUser = generateUsuario(1)
        val targetUser = generateUsuario(2)
        val oldData = dataSource.getUsuarioById(targetUser.id)
        //Act
        dataSource.followOrUnfollow(currentUser.id, targetUser.id)
        //Assert
        val targetUserResult = dataSource.getUsuarioById(targetUser.id)
        Truth.assertThat(targetUserResult?.followersCount).isGreaterThan(oldData?.followersCount)
    }

    @Test
    fun followOrUnfollow_unfollow_followedFalse() = runTest {
        //Arrange
        val currentUser = generateUsuario(1)
        val targetUser = generateUsuario(2)
        //Act
        dataSource.followOrUnfollow(currentUser.id, targetUser.id)
        dataSource.followOrUnfollow(currentUser.id, targetUser.id)
        //Assert
        val targetUserResult = dataSource.getUsuarioById(targetUser.id, currentUser.id)
        Truth.assertThat(targetUserResult?.followed).isFalse()
    }

    @Test
    fun getSeguidores_afterFollow_containsFollower() = runTest {
        //Arrange
        val currentUser = generateUsuario(1)
        val targetUser = generateUsuario(2)
        dataSource.followOrUnfollow(currentUser.id, targetUser.id)
        //Act
        val seguidores = dataSource.getSeguidores(targetUser.id)
        //Assert
        Truth.assertThat(seguidores).hasSize(1)
        Truth.assertThat(seguidores.first().id).isEqualTo(currentUser.id)
    }

    @Test
    fun getReviewsDeUsuario_validId_correctReviews() = runTest {
        //Arrange
        val usuario = generateUsuario(1)
        //Act
        val reviews = dataSource.getReviewsDeUsuario(usuario.id)
        //Assert
        Truth.assertThat(reviews).isNotEmpty()
        Truth.assertThat(reviews.first().usuarioId).isEqualTo(usuario.id)
    }
}
