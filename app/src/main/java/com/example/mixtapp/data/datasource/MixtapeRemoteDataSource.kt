package com.example.mixtapp.data.datasource

import com.example.mixtapp.data.dtos.CreateItemMixtapeDto
import com.example.mixtapp.data.dtos.CreateMixtapeDto
import com.example.mixtapp.data.dtos.ItemMixtapeDto
import com.example.mixtapp.data.dtos.MixtapeDto
import kotlinx.coroutines.flow.Flow

// Las mixtapes de los usuarios: llegan en vivo, asi que agregar o quitar un item se ve sin recargar
interface MixtapeRemoteDataSource {
    // Las de un usuario. Con el id de un item se marca en cuales ya esta
    fun listenMixtapesDeUsuario(usuarioId: String, itemId: String = ""): Flow<List<MixtapeDto>>

    // null si no existe (por ejemplo, si la borraron mientras se veia)
    fun listenMixtapeById(id: String): Flow<MixtapeDto?>

    // Los items de la subcoleccion de una mixtape
    fun listenItemsDeMixtape(mixtapeId: String): Flow<List<ItemMixtapeDto>>

    // Retorna el id de la mixtape nueva
    suspend fun crearMixtape(mixtape: CreateMixtapeDto): String

    // Si el item ya esta no se repite. false si la mixtape ya no existe: el repositorio decide el error
    suspend fun agregarItem(mixtapeId: String, itemId: String, item: CreateItemMixtapeDto): Boolean

    // false si la mixtape ya no existe
    suspend fun quitarItem(mixtapeId: String, itemId: String): Boolean

    suspend fun eliminarMixtape(mixtapeId: String): Unit
}
