package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.ChatUi
import com.example.mixtapp.data.model.ForoUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.MensajeUi
import com.example.mixtapp.data.model.UsuarioDelChatUi
import java.time.Instant

// El documento foros/{itemId}: la copia del item, cuantos mensajes tiene y la fecha del ultimo.
// Los mensajes van en la subcoleccion foros/{itemId}/mensajes
data class ForoDto(
    val id: String,
    val item: CopiaDeItemDto?,
    val mensajesCount: Int,
    // En formato ISO: ordenar el texto ordena las fechas
    val actualizadoEn: String?,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", null, 0, null)
}

// Un mensaje de un foro (foros/{itemId}/mensajes) o de un chat (chats/{chatId}/mensajes)
data class MensajeDto(
    val id: String,
    val usuarioId: String,
    // Copia de quien lo escribio, como en los comentarios: no hay joins
    val usuario: UsuarioDto?,
    val texto: String,
    val createdAt: String?,
) {
    constructor() : this("", "", null, "", null)
}

// Lo que se manda para crear un mensaje: lo mismo sin el id, que lo pone Firestore
data class CreateMensajeDto(
    val usuarioId: String,
    val usuario: UsuarioDto,
    val texto: String,
    val createdAt: String,
)

// El documento chats/{chatId}: quienes hablan (para buscar mis chats), la copia de los dos y el ultimo
// mensaje para la lista
data class ChatDto(
    val id: String,
    val participantes: List<String>,
    val usuarios: List<UsuarioDto>,
    val ultimoMensaje: String,
    val actualizadoEn: String?,
) {
    constructor() : this("", emptyList(), emptyList(), "", null)
}

fun ForoDto.toForoUi(ahora: Instant = Instant.now()): ForoUi? {
    val copia = item ?: return null

    return ForoUi(
        item = ItemResumenUi(
            id = id,
            tipo = copia.tipo,
            titulo = copia.titulo,
            subtitulo = copia.subtitulo,
            imagenUrl = copia.imagenUrl,
            genero = copia.genero,
        ),
        mensajes = mensajesCount,
        timeAgo = haceCuanto(fechaIso = actualizadoEn, ahora = ahora),
    )
}

fun MensajeDto.toMensajeUi(miId: String, ahora: Instant = Instant.now()): MensajeUi {
    return MensajeUi(
        id = id,
        autorId = usuarioId,
        autor = usuario?.nombre ?: "",
        autorFoto = usuario?.fotoUrl ?: "",
        texto = texto,
        timeAgo = haceCuanto(fechaIso = createdAt, ahora = ahora),
        esMio = usuarioId == miId,
    )
}

// En la lista de chats se muestra a la otra persona; null si el documento no la tiene
fun ChatDto.toChatUi(miId: String, ahora: Instant = Instant.now()): ChatUi? {
    val otro = usuarios.firstOrNull { it.id != miId } ?: return null

    return ChatUi(
        id = id,
        otro = UsuarioDelChatUi(id = otro.id, nombre = otro.nombre, fotoUrl = otro.fotoUrl ?: ""),
        ultimoMensaje = ultimoMensaje,
        timeAgo = haceCuanto(fechaIso = actualizadoEn, ahora = ahora),
    )
}
