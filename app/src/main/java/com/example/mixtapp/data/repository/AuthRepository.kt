package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

// Unifica las fuentes de autenticacion y expone solo lo que la capa de UI necesita
class AuthRepository @Inject constructor(
    private val authRemoteDataSource: AuthRemoteDataSource
) {

    val currentUser: FirebaseUser?
        get() = authRemoteDataSource.currentUser

    suspend fun signIn(email: String, password: String) {
        authRemoteDataSource.signIn(email = email, password = password)
    }

    suspend fun signUp(email: String, password: String) {
        authRemoteDataSource.signUp(email = email, password = password)
    }

    fun signOut() {
        authRemoteDataSource.signOut()
    }
}
