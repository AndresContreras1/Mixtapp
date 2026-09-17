package com.example.mixtapp.data.repository

import android.net.Uri
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.StorageRemoteDataSource
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.storage.StorageException
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
        } catch (e: FirebaseNetworkException) {
            Result.failure(SinConexionException())
        } catch (e: StorageException) {
            Result.failure(errorDeStorage(codigo = e.errorCode))
        } catch (e: Exception) {
            Result.failure(ErrorAlSubirImagenException())
        }
    }

    private fun errorDeStorage(codigo: Int): Exception = when (codigo) {
        StorageException.ERROR_QUOTA_EXCEEDED -> CuotaExcedidaException()
        StorageException.ERROR_NOT_AUTHORIZED -> PermisoDenegadoException()
        StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> SinConexionException()
        else -> ErrorAlSubirImagenException()
    }
}
