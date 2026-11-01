package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.HistoriaUi
import com.example.mixtapp.data.model.SeccionUi

// Igual al documento de la coleccion historias de Firestore, cuyo id es el del item. Va aparte del
// item para que el inicio no descargue todo este texto: solo se lee al abrir Datos curiosos
data class HistoriaDto(
    val tipo: String,
    val secciones: List<SeccionDto>,
) {
    // Firestore necesita un constructor vacio para convertir el documento en el DTO
    constructor() : this("", emptyList())
}

data class SeccionDto(
    val titulo: String,
    val texto: String,
) {
    constructor() : this("", "")
}

// Una seccion sin texto no se muestra
fun HistoriaDto.toHistoriaUi(): HistoriaUi {
    return HistoriaUi(
        secciones = secciones
            .filter { it.texto.isNotBlank() }
            .map { SeccionUi(titulo = it.titulo, texto = it.texto) },
    )
}
