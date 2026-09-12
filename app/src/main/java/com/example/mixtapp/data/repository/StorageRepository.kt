package com.example.mixtapp.data.repository

import android.net.Uri
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.StorageRemoteDataSource
import javax.inject.Inject

class StorageRepository @Inject constructor(
    private val storage: StorageRemoteDataSource,
    private val auth: AuthRemoteDataSource
) {

    suspend fun uploadProfileImage(uri: Uri): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val path = "profileImages/$userId.jpg"
            val url = storage.uploadImage(path = path, uri = uri)
            auth.updateProfileImage(photoUrl = url)

            Result.success(url)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
