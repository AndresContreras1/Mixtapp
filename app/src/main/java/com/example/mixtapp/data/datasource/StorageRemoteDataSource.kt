package com.example.mixtapp.data.datasource

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

// Sube archivos a Firebase Storage. Es general: el repositorio decide la ruta de cada archivo
class StorageRemoteDataSource @Inject constructor(
    private val storage: FirebaseStorage
) {

    // Sube la imagen de la galeria y retorna la URL publica para guardarla en el documento
    suspend fun uploadImage(path: String, uri: Uri): String {
        val imageRef = storage.reference.child(path)
        imageRef.putFile(uri).await()
        return imageRef.downloadUrl.await().toString()
    }
}
