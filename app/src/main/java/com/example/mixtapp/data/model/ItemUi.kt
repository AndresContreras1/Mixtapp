package com.example.mixtapp.data.model

// Lo que se puede calificar. Es el mismo texto que guarda la base en el campo tipo
const val TIPO_ALBUM = "album"
const val TIPO_CANCION = "cancion"
const val TIPO_ARTISTA = "artista"

// Un album, una cancion o un artista con todo lo que muestran el detalle y las filas del inicio
data class ItemUi(
    val id: String,
    val tipo: String,
    val titulo: String,
    // Album: el artista. Cancion: el album. Artista: su banda, o el genero si es solista
    val subtitulo: String,
    // Cancion: el artista. Album y artista: vacio
    val detalle: String,
    val imagenUrl: String,
    // Vacio en los artistas
    val anio: String,
    // Album y cancion: la fecha de salida AAAA-MM-DD, para ordenar lo mas nuevo. Vacia en los artistas
    val fechaLanzamiento: String,
    // Quien lo hizo: el artista del album o la cancion, o la banda del artista (el mismo si es
    // solista). Con el se arman los lanzamientos de lo que me gusta
    val artista: String,
    val genero: String,
    val subgenero: String,
    // Puesto en la lista de Deezer de su genero; null si no esta en la lista
    val tendencia: Int?,
    val likes: Int,
    val likesText: String,
    // Para abrirlo afuera de la app: la busqueda en YouTube (solo canciones) y en Spotify, y el
    // Instagram del artista con su link. Vacios si no tiene
    val youtubeUrl: String = "",
    val spotifyUrl: String = "",
    val instagram: String = "",
    val instagramUrl: String = "",
    // No estan en el documento del item: el like lo busca el data source con mi uid y el
    // guardado sale de mis guardados
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
)

// Lo poco que se copia de un item en una resena, un like, un guardado o una notificacion:
// suficiente para pintar la tarjeta y abrir su detalle
data class ItemResumenUi(
    val id: String,
    val tipo: String,
    val titulo: String,
    val subtitulo: String,
    val imagenUrl: String,
    // Solo lo traen los likes: con el genero se arma el For you
    val genero: String = "",
)

// Un item con las estadisticas que salen de sus resenas
data class ItemConResenasUi(
    val item: ItemUi,
    val rating: Double,
    // El promedio ya redondeado, para pintar las estrellas
    val estrellas: Int,
    val ratingCount: Int,
    val recommendRate: Int,
    // Mi calificacion; 0 si no lo he calificado
    val userRating: Int,
    val reviews: List<ReviewUi>,
) {
    // Para ordenar las filas de populares: cuenta igual un like que una resena
    val popularidad: Int get() = item.likes + reviews.size
}

fun ItemUi.toItemResumenUi(): ItemResumenUi {
    return ItemResumenUi(
        id = id,
        tipo = tipo,
        titulo = titulo,
        subtitulo = subtitulo,
        imagenUrl = imagenUrl,
        genero = genero,
    )
}
