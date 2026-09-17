package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
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

    suspend fun signIn(email: String, password: String): Result<Unit> {
        return try {
            authRemoteDataSource.signIn(email = email, password = password)
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(UsuarioNoExisteException())
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(CredencialesInvalidasException())
        } catch (e: FirebaseNetworkException) {
            Result.failure(SinConexionException())
        } catch (e: FirebaseTooManyRequestsException) {
            Result.failure(DemasiadosIntentosException())
        } catch (e: Exception) {
            Result.failure(ErrorDeInicioSesionException())
        }
    }

    suspend fun signUp(email: String, password: String): Result<Unit> {
        return try {
            authRemoteDataSource.signUp(email = email, password = password)
            Result.success(Unit)
        } catch (e: FirebaseAuthUserCollisionException) {
            Result.failure(CorreoYaRegistradoException())
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(CredencialesInvalidasException())
        } catch (e: FirebaseNetworkException) {
            Result.failure(SinConexionException())
        } catch (e: FirebaseTooManyRequestsException) {
            Result.failure(DemasiadosIntentosException())
        } catch (e: Exception) {
            Result.failure(ErrorDeRegistroException())
        }
    }

    fun signOut() {
        authRemoteDataSource.signOut()
    }
}
