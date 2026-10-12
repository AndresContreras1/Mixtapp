package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalFollowingProvider
import com.example.mixtapp.data.local.LocalNotificationsProvider
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.NotificationUi
import javax.inject.Inject

// Seguidos y notificaciones siguen locales. Las funciones son suspend como las remotas: si estos
// datos pasan a la base remota, el ViewModel no cambia
class SocialLocalDataSource @Inject constructor() {

    suspend fun getFollowing(): FollowingUi = LocalFollowingProvider.following

    suspend fun getNotifications(): List<NotificationUi> = LocalNotificationsProvider.notifications

    suspend fun guardarFollowing(following: FollowingUi): Unit {
        LocalFollowingProvider.following = following
    }
}
