package com.example.mixtapp.data.datasource

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// El token de FCM identifica este celular para enviarle notificaciones. FirebaseMessaging llega por
// el constructor como las demas instancias de Firebase, asi las pruebas lo pueden moquear
class MessagingRemoteDataSource @Inject constructor(
    private val messaging: FirebaseMessaging
) {

    // Sin el await sigue derecho y el token queda vacio
    suspend fun getToken(): String = messaging.token.await()
}
