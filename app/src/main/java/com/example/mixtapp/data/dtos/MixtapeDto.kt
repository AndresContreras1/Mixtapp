package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MixtapeUi

// El documento mixtapes/{id}: una lista de albumes, canciones y artistas que arma un usuario. Los
// items van en la subcoleccion mixtapes/{id}/items, como los likes: el documento solo trae lo que
// pinta la tarjeta (cuantos son y las portadas de los primeros), asi la lista del perfil no baja
// todos los items de cada mixtape
data class MixtapeDto(
    val id: String,
    val nombre: String,
    val descripcion: String?,
    // Quien la creo, con la copia de su nombre y su foto, como en la resena: si los cambia, la
    // Cloud Function actualiza la copia
    val usuarioId: String,
    val usuario: UsuarioDto?,
    val itemsCount: Int,
    // Las portadas de los primeros items, hasta cuatro, para el mosaico de la tarjeta
    val portadas: List<String>,
    // En formato ISO: ordena mis mixtapes de la mas nueva a la mas vieja
    val createdAt: String?,
    // No esta en la base: el data source lo llena cuando se pregunta por un item, para "Ya esta"
    var tieneElItem: Boolean = false,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", null, "", null, 0, emptyList(), null)
}

// Un documento de mixtapes/{id}/items: la copia del item, con el id del item como id del documento
data class ItemMixtapeDto(
    val id: String,
    val tipo: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
    // En formato ISO: los items se muestran en el orden en que se agregaron
    val agregadoEn: String?,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", "", "", null)
}

// Lo que se manda para crear una mixtape. Empieza sin items
data class CreateMixtapeDto(
    val nombre: String,
    val descripcion: String?,
    val usuarioId: String,
    val usuario: CreateReviewUsuarioDto,
    val itemsCount: Int,
    val portadas: List<String>,
    val createdAt: String,
)

// Lo que se manda al agregar un item: solo lo que se muestra de el
data class CreateItemMixtapeDto(
    val tipo: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
    val agregadoEn: String,
)

fun ItemUi.toCreateItemMixtapeDto(agregadoEn: String): CreateItemMixtapeDto {
    return CreateItemMixtapeDto(
        tipo = tipo,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
        agregadoEn = agregadoEn,
    )
}

fun ItemMixtapeDto.toItemResumenUi(): ItemResumenUi {
    return ItemResumenUi(
        id = id,
        tipo = tipo,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
    )
}

fun MixtapeDto.toMixtapeUi(usuarioActualId: String): MixtapeUi {
    return MixtapeUi(
        id = id,
        nombre = nombre,
        descripcion = descripcion ?: "",
        usuarioNombre = usuario?.nombre ?: "",
        usuarioFotoUrl = usuario?.fotoUrl ?: "",
        itemsCount = itemsCount,
        portadas = portadas,
        isMine = usuarioId == usuarioActualId,
        tieneElItem = tieneElItem,
    )
}
