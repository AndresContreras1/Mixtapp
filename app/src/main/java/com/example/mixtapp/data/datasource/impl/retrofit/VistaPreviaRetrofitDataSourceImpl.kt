package com.example.mixtapp.data.datasource.impl.retrofit

import com.example.mixtapp.data.datasource.VistaPreviaRemoteDataSource
import com.example.mixtapp.data.datasource.services.DeezerRetrofitService
import javax.inject.Inject

// Busca la cancion en Deezer y entrega el enlace de su fragmento. Solo se pide la primera: Deezer
// ordena por parecido con la busqueda
class VistaPreviaRetrofitDataSourceImpl @Inject constructor(
    private val deezerRetrofitService: DeezerRetrofitService,
) : VistaPreviaRemoteDataSource {

    override suspend fun getVistaPrevia(busqueda: String): String? {
        val canciones = deezerRetrofitService.buscarCanciones(busqueda = busqueda, limite = 1).data

        return canciones.firstOrNull()?.preview?.ifEmpty { null }
    }
}
