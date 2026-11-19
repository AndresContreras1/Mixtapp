package com.example.mixtapp.data.model

// En que va el fragmento que suena en la app
enum class EstadoDeReproduccion {
    // Descargando el inicio del audio
    Cargando,
    Sonando,
    // Llego al final de los 30 segundos
    Terminada,
}

// Lo que dura el fade: el volumen sube en el primer segundo y medio y baja en los ultimos dos
const val FADE_DE_ENTRADA_MS = 1500
const val FADE_DE_SALIDA_MS = 2000

// Que se busca en Deezer para que suene algo del item: la cancion con su artista, el album con su
// artista (suena su cancion mas parecida) y, del artista, su nombre (suena su cancion mas popular)
fun busquedaDeVistaPrevia(item: ItemUi): String = when (item.tipo) {
    TIPO_CANCION, TIPO_ALBUM -> "${item.artista} ${item.titulo}"
    else -> item.titulo
}

// El volumen del fade, de 0 a 1, segun en que milisegundo va el audio y cuanto dura. Sin duracion
// todavia (el audio no ha cargado) solo cuenta la entrada
fun volumenDelFade(posicionMs: Int, duracionMs: Int): Float {
    val entrada = posicionMs.toFloat() / FADE_DE_ENTRADA_MS
    val salida = if (duracionMs > 0) (duracionMs - posicionMs).toFloat() / FADE_DE_SALIDA_MS else 1f

    return minOf(entrada, salida, 1f).coerceAtLeast(0f)
}
