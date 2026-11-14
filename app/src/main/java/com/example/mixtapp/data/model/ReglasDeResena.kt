package com.example.mixtapp.data.model

import java.time.LocalDate
import java.time.format.DateTimeParseException

// Limite de caracteres de la resena y de cada comentario de su discusion
const val MAX_REVIEW_LENGTH = 500
const val MAX_COMMENT_LENGTH = 500

// La fecha va como AAAA-MM-DD, igual que la guarda la base
const val MAX_DATE_LENGTH = 10

// La fecha de escucha es opcional; si se escribe, tiene que ser AAAA-MM-DD, existir en el
// calendario (no hay 31 de febrero) y no ser despues de hoy
fun esFechaDeEscuchaValida(fecha: String, hoy: LocalDate = LocalDate.now()): Boolean {
    if (fecha.isEmpty()) return true

    val dia = try {
        LocalDate.parse(fecha)
    } catch (e: DateTimeParseException) {
        return false
    }

    return !dia.isAfter(hoy)
}
