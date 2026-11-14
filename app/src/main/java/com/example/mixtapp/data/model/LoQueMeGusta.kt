package com.example.mixtapp.data.model

// Desde esta calificacion mi resena cuenta como que el item me gusta
const val CALIFICACION_QUE_ME_GUSTA = 3

// Lo que me gusta: lo que tiene mi like y lo que resene con 3 estrellas o mas. Una resena con mala
// nota no cuenta. Un item que cumple las dos cosas sale dos veces: en el inicio su genero pesa doble.
// La usan el inicio (For you y Descubre) y Tu año en Mixtapp
fun itemsQueMeGustan(
    items: List<ItemUi>,
    idsConMiLike: Set<String>,
    // La calificacion que le puse a cada item, por su id
    misCalificaciones: Map<String, Int>,
): List<ItemUi> {
    val conMiLike = items.filter { it.id in idsConMiLike }
    val bienCalificados = items.filter { (misCalificaciones[it.id] ?: 0) >= CALIFICACION_QUE_ME_GUSTA }

    return conMiLike + bienCalificados
}
