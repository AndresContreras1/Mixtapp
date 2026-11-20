package com.example.mixtapp.data.model

// Una persona con gustos parecidos a los mios, aunque no la siga: cuanto coincidimos (0 a 100), lo
// que nos gusta a los dos y el genero que mas compartimos
data class AfinidadUi(
    val usuario: UsuarioUi,
    val porcentaje: Int,
    // Hasta tres items que nos gustan a los dos, para mostrar sus portadas
    val enComun: List<ItemResumenUi>,
    // null si no compartimos ningun genero
    val generoEnComun: String?,
)

// El foro de un album, una cancion o un artista: la copia del item, cuantos mensajes tiene y hace
// cuanto se escribio el ultimo
data class ForoUi(
    val item: ItemResumenUi,
    val mensajes: Int,
    val timeAgo: String,
)

// Un mensaje de un foro o de un chat: quien lo escribio, el texto y hace cuanto
data class MensajeUi(
    val id: String,
    val autorId: String,
    val autor: String,
    val autorFoto: String,
    val texto: String,
    val timeAgo: String,
    // Los mios van a la derecha en el chat
    val esMio: Boolean,
)

// Una conversacion directa: con quien, el ultimo mensaje y hace cuanto
data class ChatUi(
    val id: String,
    val otro: UsuarioDelChatUi,
    val ultimoMensaje: String,
    val timeAgo: String,
)

// Lo que se muestra de la otra persona en un chat
data class UsuarioDelChatUi(
    val id: String,
    val nombre: String,
    val fotoUrl: String,
)
