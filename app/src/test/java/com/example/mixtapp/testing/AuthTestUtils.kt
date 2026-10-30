package com.example.mixtapp.testing

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.tasks.await

private const val CLAVE_DE_PRUEBA = "Prueba123!"

// Crea (o reutiliza) un usuario en el emulador de Auth y deja la sesion iniciada. Devuelve su uid
suspend fun iniciarSesionDePrueba(email: String): String {
    val auth = FirebaseAuth.getInstance()
    try {
        auth.createUserWithEmailAndPassword(email, CLAVE_DE_PRUEBA).await()
    } catch (e: FirebaseAuthUserCollisionException) {
        auth.signInWithEmailAndPassword(email, CLAVE_DE_PRUEBA).await()
    }
    return auth.currentUser!!.uid
}

// Borrar el usuario tambien cierra la sesion
suspend fun cerrarSesionDePrueba() {
    FirebaseAuth.getInstance().currentUser?.delete()?.await()
    FirebaseAuth.getInstance().signOut()
}
