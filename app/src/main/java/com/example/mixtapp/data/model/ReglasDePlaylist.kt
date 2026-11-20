package com.example.mixtapp.data.model

// Cuantas canciones lleva cada playlist y cuantas mezclas de genero se arman
const val CANCIONES_POR_PLAYLIST = 15
const val MEZCLAS_DE_GENERO = 2

// Que tipo de playlist es: el titulo y la descripcion los pone la pantalla segun el tipo
enum class TipoDePlaylist {
    // Lo mas popular de uno de mis generos
    MezclaDeGenero,
    // De mis generos, lo que todavia no tiene mi like ni mi resena
    Descubre,
    // El Top Colombia de Deezer, por su puesto
    TopColombia,
    // Lo mas popular de todo el catalogo, para quien todavia no tiene likes
    LoMasEscuchado,
}

// Una playlist hecha para mi: su id (con el se abre su pantalla), su tipo, el genero si es una
// mezcla y sus canciones en orden. Las portadas del mosaico son las de sus primeras cuatro canciones
data class PlaylistUi(
    val id: String,
    val tipo: TipoDePlaylist,
    val genero: String?,
    val canciones: List<ItemUi>,
) {
    val portadas: List<String> get() = canciones.take(4).map { it.imagenUrl }
}

// Lo que me gusta del catalogo: lo que tiene mi like y lo que resene bien (la regla esta en
// itemsQueMeGustan). Solo cuenta lo que tiene genero. La usan el inicio y las playlists
fun loQueMeGustaDelCatalogo(catalogo: List<ItemConResenasUi>, idsConMiLike: Set<String>): List<ItemUi> {
    val misCalificaciones = catalogo.flatMap { conResenas ->
        conResenas.reviews.filter { it.isMine }.map { conResenas.item.id to it.rating }
    }.toMap()

    return itemsQueMeGustan(
        items = catalogo.map { it.item },
        idsConMiLike = idsConMiLike,
        misCalificaciones = misCalificaciones,
    ).filter { it.genero.isNotEmpty() }
}

// Las playlists hechas para mi, siempre las mismas para el mismo catalogo y los mismos gustos, asi
// la pantalla de una playlist la vuelve a armar con su id:
// - una mezcla por cada uno de mis dos generos con mas likes
// - Descubre: de mis generos, lo que todavia no me gusta, primero lo que esta en tendencia
// - Top Colombia, si alguna cancion esta en esa lista
// - Lo mas escuchado, que siempre esta
fun armarPlaylists(catalogo: List<ItemConResenasUi>, meGustan: List<ItemUi>): List<PlaylistUi> {
    val canciones = catalogo.filter { it.item.tipo == TIPO_CANCION }
    val porPopularidad = canciones.sortedByDescending { it.popularidad }
    val idsQueMeGustan = meGustan.map { it.id }.toSet()
    val misGeneros = meGustan.groupingBy { it.genero }.eachCount().entries
        .sortedByDescending { it.value }
        .map { it.key }

    val mezclas = misGeneros.take(MEZCLAS_DE_GENERO).map { genero ->
        PlaylistUi(
            id = "mezcla_$genero",
            tipo = TipoDePlaylist.MezclaDeGenero,
            genero = genero,
            canciones = porPopularidad.filter { it.item.genero == genero }.take(CANCIONES_POR_PLAYLIST).map { it.item },
        )
    }

    val descubre = PlaylistUi(
        id = "descubre",
        tipo = TipoDePlaylist.Descubre,
        genero = null,
        canciones = canciones
            .filter { it.item.genero in misGeneros && it.item.id !in idsQueMeGustan }
            .sortedWith(compareBy<ItemConResenasUi> { it.item.tendencia ?: Int.MAX_VALUE }.thenByDescending { it.popularidad })
            .take(CANCIONES_POR_PLAYLIST)
            .map { it.item },
    )

    val topColombia = PlaylistUi(
        id = "top_colombia",
        tipo = TipoDePlaylist.TopColombia,
        genero = null,
        canciones = canciones
            .filter { it.item.tendenciaColombia != null }
            .sortedBy { it.item.tendenciaColombia }
            .take(CANCIONES_POR_PLAYLIST)
            .map { it.item },
    )

    val loMasEscuchado = PlaylistUi(
        id = "lo_mas_escuchado",
        tipo = TipoDePlaylist.LoMasEscuchado,
        genero = null,
        canciones = porPopularidad.take(CANCIONES_POR_PLAYLIST).map { it.item },
    )

    // Una playlist sin canciones no se muestra
    return (mezclas + descubre + topColombia + loMasEscuchado).filter { it.canciones.isNotEmpty() }
}
