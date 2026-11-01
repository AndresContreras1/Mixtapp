package com.example.mixtapp.data.dtos

import java.net.URLEncoder
import java.time.Duration
import java.time.Instant

// Los numeros y las fechas se dejan listos para la pantalla en el mapeo, como en clase:
// 14 929 likes se muestran como 14K

// 950 -> "950", 1 200 -> "1.2K", 14 929 -> "14K", 2 500 000 -> "2.5M"
fun formatoCantidad(cantidad: Int): String = when {
    cantidad < 1_000 -> cantidad.toString()
    cantidad < 10_000 -> "${cantidad / 1_000}.${cantidad % 1_000 / 100}K"
    cantidad < 1_000_000 -> "${cantidad / 1_000}K"
    else -> "${cantidad / 1_000_000}.${cantidad % 1_000_000 / 100_000}M"
}

// Cuanto paso desde una fecha ISO: "5 min", "3 h", "2 d"; desde una semana, la fecha AAAA-MM-DD.
// Con menos de un minuto queda vacio y la pantalla muestra "ahora". ahora llega como parametro
// para poder probar el mapeo con una fecha fija
fun haceCuanto(fechaIso: String?, ahora: Instant): String {
    if (fechaIso.isNullOrEmpty()) return ""

    // Una fecha que no esta en formato ISO completo se muestra tal cual, solo el dia
    val fecha = try {
        Instant.parse(fechaIso)
    } catch (e: Exception) {
        return fechaIso.take(10)
    }

    val minutos = Duration.between(fecha, ahora).toMinutes()
    return when {
        minutos < 1 -> ""
        minutos < 60 -> "$minutos min"
        minutos < 60 * 24 -> "${minutos / 60} h"
        minutos < 60 * 24 * 7 -> "${minutos / (60 * 24)} d"
        else -> fechaIso.take(10)
    }
}

// Los links para abrir el item afuera de la app. No se guardan en la base: salen del titulo y el
// artista. URLEncoder deja los espacios como +, y en estos links van como %20
fun busquedaEnYoutube(texto: String): String =
    "https://www.youtube.com/results?search_query=${codificar(texto)}"

fun busquedaEnSpotify(texto: String): String = "https://open.spotify.com/search/${codificar(texto)}"

// "metallica" -> "https://www.instagram.com/metallica/"; sin usuario queda vacio
fun perfilDeInstagram(usuario: String?): String =
    if (usuario.isNullOrEmpty()) "" else "https://www.instagram.com/$usuario/"

private fun codificar(texto: String): String = URLEncoder.encode(texto, "UTF-8").replace("+", "%20")
