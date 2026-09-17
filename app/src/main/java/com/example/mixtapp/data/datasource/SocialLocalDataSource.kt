package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.local.LocalFollowingProvider
import com.example.mixtapp.data.local.LocalFriendActivityProvider
import com.example.mixtapp.data.local.LocalNotificationsProvider
import com.example.mixtapp.data.local.LocalProfileProvider
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.ProfileUi
import javax.inject.Inject

class SocialLocalDataSource @Inject constructor() {

    suspend fun getFollowing(): FollowingUi = LocalFollowingProvider.following

    suspend fun getFriendActivity(): FriendActivityUi = LocalFriendActivityProvider.friendActivity

    suspend fun getNotifications(): List<NotificationUi> = LocalNotificationsProvider.notifications

    suspend fun getProfile(): ProfileUi = LocalProfileProvider.profile
}
