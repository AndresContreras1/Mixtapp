package com.example.mixtapp.data.model

// Los meses cortos para mostrar una fecha en una sola forma en toda la app: "3 mar 1986"
private val MESES = listOf("ene", "feb", "mar", "abr", "may", "jun", "jul", "ago", "sep", "oct", "nov", "dic")

// "1986-03-03" -> "3 mar 1986". Sin anio de mas (el mismo de hoy) queda "3 mar". Una fecha que no
// tiene el formato AAAA-MM-DD se deja como llego
fun fechaConMes(fechaIso: String, anioActual: Int?): String {
    val partes = fechaIso.take(10).split("-")
    val anio = partes.getOrNull(0)?.toIntOrNull()
    val mes = partes.getOrNull(1)?.toIntOrNull()
    val dia = partes.getOrNull(2)?.toIntOrNull()
    if (anio == null || mes == null || dia == null || mes !in 1..12) return fechaIso

    return if (anio == anioActual) "$dia ${MESES[mes - 1]}" else "$dia ${MESES[mes - 1]} $anio"
}
