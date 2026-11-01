package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ItemUi

// Lo que se lee de usuarios/{uid}/likes y usuarios/{uid}/guardados: la copia del item que le
// gusto o que guardo. El id del documento es el id del item
data class ItemDeUsuarioDto(
    val id: String,
    val tipo: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
    // Con los generos de mis likes se arma el For you
    val genero: String,
    // En formato ISO: los favoritos y los guardados van del mas nuevo al mas viejo
    val createdAt: String?,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", "", "", "", "", "", null)
}

// Lo que se guarda: lo mismo sin el id, que es el nombre del documento
data class CopiaDeItemDto(
    val tipo: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
    val genero: String,
    val createdAt: String,
)

fun ItemDeUsuarioDto.toItemResumenUi(): ItemResumenUi {
    return ItemResumenUi(
        id = id,
        tipo = tipo,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
        genero = genero,
    )
}

fun ItemUi.toCopiaDeItemDto(createdAt: String): CopiaDeItemDto {
    return CopiaDeItemDto(
        tipo = tipo,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
        genero = genero,
        createdAt = createdAt,
    )
}
