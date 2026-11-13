package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.SonandoUi
import java.time.Instant

// El documento sonando/{uid}: lo que el usuario esta escuchando. Uno por usuario, asi que poner
// otro reemplaza al anterior
data class SonandoDto(
    val id: String,
    // Copia de lo que se muestra de quien escucha, como en la resena: no hay joins
    val usuario: UsuarioDto?,
    val itemId: String,
    val tipo: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
    // Quien reacciono esta en la subcoleccion reacciones; aqui solo cuantos son
    val reaccionesCount: Int,
    // En formato ISO: ordena la fila y deja fuera lo que lleva mucho tiempo
    val actualizadoEn: String?,
    // No esta en la base: el data source lo llena buscando mi reaccion en la subcoleccion
    var reaccionado: Boolean = false,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", null, "", "", "", "", "", 0, null)
}

// Lo que se manda al poner algo a sonar. Empieza sin reacciones
data class CreateSonandoDto(
    val usuario: CreateReviewUsuarioDto,
    val itemId: String,
    val tipo: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
    val reaccionesCount: Int,
    val actualizadoEn: String,
)

// El documento sonando/{uid}/reacciones/{itemId}_{mi uid}: lleva el item en el id para que una
// reaccion a lo que sonaba antes no cuente para lo que suena ahora
data class CreateReaccionDto(
    val usuarioId: String,
    val itemId: String,
    val nombre: String,
    val fotoUrl: String?,
    val createdAt: String,
)

fun SonandoDto.toSonandoUi(usuarioActualId: String, ahora: Instant = Instant.now()): SonandoUi {
    return SonandoUi(
        usuarioId = id,
        nombre = usuario?.nombre ?: "",
        fotoUrl = usuario?.fotoUrl ?: "",
        item = ItemResumenUi(
            id = itemId,
            tipo = tipo,
            titulo = titulo,
            subtitulo = subtitulo,
            imagenUrl = imagenUrl,
        ),
        timeAgo = haceCuanto(fechaIso = actualizadoEn, ahora = ahora),
        reacciones = reaccionesCount,
        reaccionado = reaccionado,
        esMio = id == usuarioActualId,
    )
}
