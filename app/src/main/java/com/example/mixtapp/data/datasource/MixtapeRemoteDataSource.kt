package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.CreateMixtapeDto
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import kotlinx.coroutines.flow.Flow

// Las mixtapes de los usuarios: llegan en vivo, asi que agregar o quitar un item se ve sin recargar
interface MixtapeRemoteDataSource {
    // Las de un usuario, la mas nueva primero
    fun listenMixtapesDeUsuario(usuarioId: String): Flow<List<MixtapeDto>>

    // null si no existe (por ejemplo, si la borraron mientras se veia)
    fun listenMixtapeById(id: String): Flow<MixtapeDto?>

    // Retorna el id de la mixtape nueva
    suspend fun crearMixtape(mixtape: CreateMixtapeDto): String

    // Si el item ya esta en la mixtape no se repite
    suspend fun agregarItem(mixtapeId: String, item: ItemMixtapeDto): Unit

    suspend fun quitarItem(mixtapeId: String, itemId: String): Unit

    suspend fun eliminarMixtape(mixtapeId: String): Unit
}
