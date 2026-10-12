package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.impl.UsuarioRetrofitDataSourceImpl
import com.example.mixtapp.data.dtos.toMyReviewUi
import com.example.mixtapp.data.dtos.toUsuarioUi
import com.example.mixtapp.data.model.MyReviewUi
import com.example.mixtapp.data.model.UsuarioUi
import retrofit2.HttpException
import javax.inject.Inject

class UsuarioRepository @Inject constructor(
    private val usuarioRemoteDataSource: UsuarioRetrofitDataSourceImpl
) {

    suspend fun getUsuarioById(id: String): Result<UsuarioUi> {
        return try {
            val usuario = usuarioRemoteDataSource.getUsuarioById(id)
            Result.success(usuario.toUsuarioUi())
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "getUsuarioById: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "getUsuarioById: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getReviewsDeUsuario(id: String): Result<List<MyReviewUi>> {
        return try {
            val reviews = usuarioRemoteDataSource.getReviewsDeUsuario(id)
            val resenas = reviews.map { it.toMyReviewUi() }
            Result.success(resenas)
        } catch (e: HttpException) {
            Log.d("UsuarioRepository", "getReviewsDeUsuario: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: Exception) {
            Log.d("UsuarioRepository", "getReviewsDeUsuario: ${e.message}")
            Result.failure(e)
        }
    }
}
