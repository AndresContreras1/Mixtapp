package com.example.mixtapp.data.model

// Por cual de los dos va un voto: el de la izquierda (a) o el de la derecha (b). Es el mismo texto
// que guarda la base en el campo opcion
const val OPCION_A = "a"
const val OPCION_B = "b"

// El duelo de hoy de un tipo (album, cancion o artista): dos del mismo genero, cuantos votos lleva
// cada uno y las opiniones de quienes votaron
data class DueloUi(
    val id: String,
    // El dia en Colombia contado desde el 1 de enero de 1970: con el se arma el id
    val dia: Int,
    val tipo: String,
    val genero: String,
    val itemA: ItemResumenUi,
    val itemB: ItemResumenUi,
    val votosA: Int,
    val votosB: Int,
    // La mas nueva primero
    val opiniones: List<VotoUi>,
    // null si todavia no vote en este duelo
    val miOpcion: String?,
) {
    val totalVotos: Int get() = votosA + votosB

    // Sin votos los dos quedan en 50, para que la barra salga pareja
    val porcentajeA: Int get() = if (totalVotos == 0) 50 else votosA * 100 / totalVotos
    val porcentajeB: Int get() = 100 - porcentajeA

    // El mejor del dia es el que tiene mas votos; con empate no hay ninguno
    val ganador: String?
        get() = when {
            votosA > votosB -> OPCION_A
            votosB > votosA -> OPCION_B
            else -> null
        }
}

// Lo que llega del documento del duelo: los votos y los dos items que se guardaron con el primer
// voto. Los items son null mientras nadie haya votado
data class DueloEnVivoUi(
    // null mientras nadie haya votado
    val genero: String?,
    val votosA: Int,
    val votosB: Int,
    val itemA: ItemResumenUi?,
    val itemB: ItemResumenUi?,
    val opiniones: List<VotoUi>,
    val miOpcion: String?,
)

// El voto de un usuario con su opinion. La opinion puede ir vacia
data class VotoUi(
    val usuarioId: String,
    val autor: String,
    val autorFoto: String,
    val opcion: String,
    val opinion: String,
    val timeAgo: String,
    val esMio: Boolean,
)
