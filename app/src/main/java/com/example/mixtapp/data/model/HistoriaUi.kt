package com.example.mixtapp.data.model

// Los datos curiosos de un item, por secciones: Historia, Cómo se hizo, Inspiración, Conciertos
// icónicos o Dato curioso, segun el tipo
data class HistoriaUi(
    val secciones: List<SeccionUi>,
)

data class SeccionUi(
    val titulo: String,
    val texto: String,
)
