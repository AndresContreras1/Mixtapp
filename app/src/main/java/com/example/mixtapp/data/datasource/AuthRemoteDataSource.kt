package com.example.mixtapp.data.datasource

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Solo consultas contra Firebase. La logica de que salio bien o mal vive en el repositorio
class AuthRemoteDataSource @Inject constructor(
    private val auth: FirebaseAuth
) {

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    suspend fun signIn(email: String, password: String): Unit {
        auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUp(email: String, password: String): Unit {
        auth.createUserWithEmailAndPassword(email, password).await()
    }

    suspend fun updateProfileImage(photoUrl: String): Unit {
        val uri = Uri.parse(photoUrl)
        currentUser?.updateProfile(
            UserProfileChangeRequest.Builder()
                .setPhotoUri(uri)
                .build()
        )?.await()
    }

    // No lleva suspend: solo borra los datos de sesion del celular, no va a la red
    fun signOut() {
        auth.signOut()
    }
}
