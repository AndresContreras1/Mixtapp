package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.MixtapeUi
import java.time.Instant

// El documento mixtapes/{id}: una lista de albumes, canciones y artistas que arma un usuario. Los
// items van dentro del documento como copia (titulo, portada): asi la lista se pinta sin otra
// consulta, como la copia del item en la resena
data class MixtapeDto(
    val id: String,
    val nombre: String,
    val descripcion: String?,
    // Quien la creo, con la copia de su nombre y su foto
    val usuarioId: String,
    val usuarioNombre: String,
    val usuarioFotoUrl: String?,
    val items: List<ItemMixtapeDto>,
    // En formato ISO: ordena mis mixtapes de la mas nueva a la mas vieja
    val createdAt: String?,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", null, "", "", null, emptyList(), null)
}

// La copia de un item dentro de la mixtape, con su id y su tipo para abrir el detalle
data class ItemMixtapeDto(
    val id: String,
    val tipo: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
    val genero: String,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", "", "", "")
}

// Lo que se manda para crear una mixtape. Empieza sin items
data class CreateMixtapeDto(
    val nombre: String,
    val descripcion: String?,
    val usuarioId: String,
    val usuarioNombre: String,
    val usuarioFotoUrl: String?,
    val items: List<ItemMixtapeDto>,
    val createdAt: String,
)

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
        isMine = usuarioId == usuarioActualId,
        timeAgo = haceCuanto(fechaIso = createdAt, ahora = ahora),
    )
}
