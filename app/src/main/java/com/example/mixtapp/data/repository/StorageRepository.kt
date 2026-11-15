package com.example.mixtapp.data.repository

import android.net.Uri
import android.util.Log
import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.StorageRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.storage.StorageException
import javax.inject.Inject

// La foto de perfil: el orquestador de tres data sources. La sube a Firebase Storage, la pone en la
// cuenta de Authentication y copia la URL en mi documento de usuarios, para que los demas la vean
class StorageRepository @Inject constructor(
    private val storage: StorageRemoteDataSource,
    private val auth: AuthRemoteDataSource,
    private val usuarioRemoteDataSource: UsuarioRemoteDataSource
) {

    // Retorna la URL de la foto nueva
    suspend fun uploadProfileImage(uri: Uri): Result<String> {
        return try {
            val userId = auth.currentUser?.uid
                ?: return Result.failure(SinSesionException())

            val path = "profileImages/$userId.jpg"
            val url = storage.uploadImage(path = path, uri = uri)
            auth.updateProfileImage(photoUrl = url)
            copiarEnMiDocumento(usuarioId = userId, fotoUrl = url)

            Result.success(url)
        } catch (e: FirebaseFirestoreException) {
            // La foto ya quedo en Storage pero no se pudo copiar en mi documento
            Log.d("StorageRepository", "uploadProfileImage: ${e.message}")
            Result.failure(errorDeFirestore(e))
        } catch (e: FirebaseNetworkException) {
            Log.d("StorageRepository", "uploadProfileImage: ${e.message}")
            Result.failure(SinConexionException())
        } catch (e: StorageException) {
            Log.d("StorageRepository", "uploadProfileImage: ${e.message}")
            Result.failure(errorDeStorage(codigo = e.errorCode))
        } catch (e: Exception) {
            Log.d("StorageRepository", "uploadProfileImage: ${e.message}")
            Result.failure(ErrorAlSubirImagenException())
        }
    }

    // Sin documento todavia no hay donde copiarla: se copia cuando complete su perfil en Editar perfil
    private suspend fun copiarEnMiDocumento(usuarioId: String, fotoUrl: String) {
        try {
            usuarioRemoteDataSource.actualizarFotoDePerfil(usuarioId, fotoUrl)
        } catch (e: FirebaseFirestoreException) {
            if (e.code != FirebaseFirestoreException.Code.NOT_FOUND) throw e
        }
    }

    // Storage dice por que fallo con un codigo: cuota, permisos, red o lo demas
    private fun errorDeStorage(codigo: Int): Exception = when (codigo) {
        StorageException.ERROR_QUOTA_EXCEEDED -> CuotaExcedidaException()
        StorageException.ERROR_NOT_AUTHORIZED -> PermisoDenegadoException()
        StorageException.ERROR_RETRY_LIMIT_EXCEEDED -> SinConexionException()
        else -> ErrorAlSubirImagenException()
    }
}
