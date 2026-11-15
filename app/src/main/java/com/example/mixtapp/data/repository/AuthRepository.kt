package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.model.usuarioDelCorreo
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

// Unifica las fuentes de autenticacion y expone solo lo que la capa de UI necesita
class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    val currentUser: FirebaseUser?
        get() = authRemoteDataSource.currentUser

    // Para una cuenta sin datos en Firestore, el usuario es la parte del correo antes de la arroba
    val usuarioDelCorreo: String
        get() = usuarioDelCorreo(authRemoteDataSource.currentUser?.email ?: "")

    // Cada error de Authentication pasa a una excepcion de la app, para que el ViewModel elija el texto
    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            authRemoteDataSource.signIn(email = email, password = password)
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.d("AuthRepository", "signIn: ${e.message}")
            Result.failure(CredencialesInvalidasException())
        } catch (e: FirebaseAuthInvalidUserException) {
            Log.d("AuthRepository", "signIn: ${e.message}")
            Result.failure(CorreoNoRegistradoException())
        } catch (e: FirebaseNetworkException) {
            Log.d("AuthRepository", "signIn: ${e.message}")
            Result.failure(SinConexionException())
        } catch (e: FirebaseTooManyRequestsException) {
            Log.d("AuthRepository", "signIn: ${e.message}")
            Result.failure(DemasiadosIntentosException())
        } catch (e: Exception) {
            Log.d("AuthRepository", "signIn: ${e.message}")
            Result.failure(ErrorDeInicioSesionException())
        }
    }

    // Al registrarse, un correo mal escrito o una contrasena debil llegan como credenciales invalidas
    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            authRemoteDataSource.signUp(email = email, password = password)
            Result.success(Unit)
        } catch (e: FirebaseAuthUserCollisionException) {
            Log.d("AuthRepository", "signUp: ${e.message}")
            Result.failure(CorreoYaRegistradoException())
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Log.d("AuthRepository", "signUp: ${e.message}")
            Result.failure(DatosDeRegistroInvalidosException())
        } catch (e: FirebaseNetworkException) {
            Log.d("AuthRepository", "signUp: ${e.message}")
            Result.failure(SinConexionException())
        } catch (e: FirebaseTooManyRequestsException) {
            Log.d("AuthRepository", "signUp: ${e.message}")
            Result.failure(DemasiadosIntentosException())
        } catch (e: Exception) {
            Log.d("AuthRepository", "signUp: ${e.message}")
            Result.failure(ErrorDeRegistroException())
        }
    }

    // Cierra la sesion de Authentication; la navegacion vuelve al inicio de sesion
    fun signOut() {
        authRemoteDataSource.signOut()
    }
}
