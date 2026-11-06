package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.MixtapeUi
import java.time.Instant

// DTO para el documento en la coleccion mixtapes
data class MixtapeDto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String? = null,
    val usuarioId: String = "",
    val usuarioNombre: String = "",
    val usuarioFotoUrl: String? = null,
    val items: List<ItemMixtapeDto> = emptyList(),
    val likesCount: Int = 0,
    val createdAt: String? = null,
    var liked: Boolean = false,
) {
    constructor() : this("", "", null, "", "", null, emptyList(), 0, null, false)
}

data class ItemMixtapeDto(
    val id: String = "",
    val tipo: String = "",
    val titulo: String = "",
    val subtitulo: String = "",
    val imagenUrl: String = "",
    val genero: String = "",
) {
    constructor() : this("", "", "", "", "", "")
}

fun ItemResumenUi.toItemMixtapeDto(): ItemMixtapeDto {
    return ItemMixtapeDto(
        id = id,
        tipo = tipo,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
        genero = genero,
    )
}

fun ItemMixtapeDto.toItemResumenUi(): ItemResumenUi {
    return ItemResumenUi(
        id = id,
        tipo = tipo,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
        genero = genero,
    )
}

fun MixtapeDto.toMixtapeUi(usuarioActualId: String, ahora: Instant = Instant.now()): MixtapeUi {
    return MixtapeUi(
        id = id,
        nombre = nombre,
        descripcion = descripcion ?: "",
        usuarioId = usuarioId,
        usuarioNombre = usuarioNombre,
        usuarioFotoUrl = usuarioFotoUrl ?: "",
        items = items.map { it.toItemResumenUi() },
        itemCount = items.size,
        isMine = usuarioId == usuarioActualId,
        likes = likesCount,
        likesText = formatoCantidad(likesCount),
        isLiked = liked,
        createdAt = createdAt ?: "",
        timeAgo = haceCuanto(fechaIso = createdAt, ahora = ahora),
    )
}
