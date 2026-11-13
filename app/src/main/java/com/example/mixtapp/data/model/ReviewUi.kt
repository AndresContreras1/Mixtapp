package com.example.mixtapp.data.model

// Una resena tal como la pintan todas las tarjetas: el detalle, el inicio, los perfiles, Mis
// resenas, Siguiendo y la discusion
data class ReviewUi(
    val id: String,
    // Para abrir el perfil de quien la escribio
    val authorId: String,
    val author: String,
    // La foto de quien la escribio; si esta vacia se muestran las iniciales
    val authorImage: String,
    // La fecha de escucha (AAAA-MM-DD) o, si no la puso, el dia en que la publico
    val date: String,
    val rating: Int,
    val content: String,
    // Cuanto hace que la publico o la edito ("3 h"); vacio si fue hace menos de un minuto
    val timeAgo: String,
    // Si la escribio el usuario de la app: el repositorio la marca al mapear
    val isMine: Boolean,
    val likes: Int,
    // Los likes listos para la pantalla: 14 929 se muestra como 14K
    val likesText: String,
    val isLiked: Boolean,
    // Cuantos comentarios tiene su discusion
    val comments: Int,
    // Lo que se califico: album, cancion o artista
    val item: ItemResumenUi,
    // En formato ISO: ordena las listas por lo ultimo que se publico o edito
    val updatedAt: String,
)
