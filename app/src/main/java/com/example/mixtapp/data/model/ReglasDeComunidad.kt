package com.example.mixtapp.data.model

import kotlin.math.roundToInt

// Cuanto pesa cada parte del match: los items que nos gustan a los dos y los generos que compartimos
const val PESO_DE_LOS_ITEMS = 0.6
const val PESO_DE_LOS_GENEROS = 0.4

// Lo mas largo que puede ser un mensaje de un foro o de un chat
const val MAX_MENSAJE = 300

// El porcentaje de afinidad entre mis likes y los de otra persona (0 a 100):
// - items: de lo que le gusta a quien tiene menos likes, cuanto le gusta tambien al otro
// - generos: cuanto se parecen las proporciones de generos de los dos (la parte que comparten)
// Sin likes de alguno de los dos no hay con que comparar y da 0
fun porcentajeDeAfinidad(misLikes: List<ItemResumenUi>, susLikes: List<ItemResumenUi>): Int {
    if (misLikes.isEmpty() || susLikes.isEmpty()) return 0

    val misIds = misLikes.map { it.id }.toSet()
    val susIds = susLikes.map { it.id }.toSet()
    val items = (misIds intersect susIds).size.toDouble() / minOf(misIds.size, susIds.size)

    val misGeneros = proporcionDeGeneros(misLikes)
    val susGeneros = proporcionDeGeneros(susLikes)
    val generos = misGeneros.keys.sumOf { genero -> minOf(misGeneros[genero] ?: 0.0, susGeneros[genero] ?: 0.0) }

    return ((items * PESO_DE_LOS_ITEMS + generos * PESO_DE_LOS_GENEROS) * 100).roundToInt()
}

// El genero que mas compartimos: el de mayor parte comun en los dos; null si no hay ninguno
fun generoEnComun(misLikes: List<ItemResumenUi>, susLikes: List<ItemResumenUi>): String? {
    val misGeneros = proporcionDeGeneros(misLikes)
    val susGeneros = proporcionDeGeneros(susLikes)

    return misGeneros.keys
        .filter { (susGeneros[it] ?: 0.0) > 0.0 }
        .maxByOrNull { minOf(misGeneros[it] ?: 0.0, susGeneros[it] ?: 0.0) }
}

// El chat entre dos personas tiene siempre el mismo id, sin importar quien escribe primero
fun idDelChat(usuarioA: String, usuarioB: String): String = listOf(usuarioA, usuarioB).sorted().joinToString("_")

// Que parte de los likes es de cada genero (suman 1). Los items sin genero no cuentan
private fun proporcionDeGeneros(likes: List<ItemResumenUi>): Map<String, Double> {
    val conGenero = likes.filter { it.genero.isNotEmpty() }
    if (conGenero.isEmpty()) return emptyMap()

    return conGenero.groupingBy { it.genero }.eachCount().mapValues { (_, cuantos) -> cuantos.toDouble() / conGenero.size }
}
