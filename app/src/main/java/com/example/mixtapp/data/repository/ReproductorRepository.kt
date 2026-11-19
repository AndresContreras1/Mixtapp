package com.example.mixtapp.data.repository

import android.util.Log
import com.example.mixtapp.data.datasource.ReproductorDataSource
import com.example.mixtapp.data.datasource.VistaPreviaRemoteDataSource
import com.example.mixtapp.data.model.EstadoDeReproduccion
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.busquedaDeVistaPrevia
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

// El fragmento de 30 segundos que suena en la app: Deezer da el enlace y el reproductor del celular
// lo hace sonar. Los data sources entregan el enlace o el audio y aqui se decide el error
class ReproductorRepository @Inject constructor(
    private val vistaPreviaRemoteDataSource: VistaPreviaRemoteDataSource,
    private val reproductorDataSource: ReproductorDataSource,
) {

    // El enlace del fragmento del item. La busqueda la arma la regla de data/model
    suspend fun getVistaPrevia(item: ItemUi): Result<String> {
        return try {
            val url = vistaPreviaRemoteDataSource.getVistaPrevia(busqueda = busquedaDeVistaPrevia(item))
                ?: return Result.failure(SinVistaPreviaException())

            Result.success(url)
        } catch (e: IOException) {
            Log.d("ReproductorRepository", "getVistaPrevia: ${e.message}")
            Result.failure(SinConexionException())
        } catch (e: HttpException) {
            Log.d("ReproductorRepository", "getVistaPrevia: ${e.message}")
            Result.failure(errorDelBackend(e))
        } catch (e: Exception) {
            Log.d("ReproductorRepository", "getVistaPrevia: ${e.message}")
            Result.failure(ErrorInesperadoException())
        }
    }

    // Suena mientras se escuche el flujo. Si el audio no abre (sin internet o el enlace vencio), el
    // ViewModel recibe la excepcion de la app en su catch
    fun escuchar(url: String): Flow<EstadoDeReproduccion> =
        reproductorDataSource.reproducir(url = url)
            .catch { e ->
                Log.d("ReproductorRepository", "escuchar: ${e.message}")
                throw if (e is IOException) SinConexionException() else errorDelFlujo(e)
            }
}
