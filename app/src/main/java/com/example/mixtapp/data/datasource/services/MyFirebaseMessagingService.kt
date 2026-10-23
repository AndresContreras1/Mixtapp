package com.example.mixtapp.data.datasource.services

import com.google.firebase.messaging.FirebaseMessagingService

// La puerta de las notificaciones push: Firebase las entrega a esta clase. Con la app en
// segundo plano o cerrada, Android las muestra solo
class MyFirebaseMessagingService : FirebaseMessagingService() {
}
