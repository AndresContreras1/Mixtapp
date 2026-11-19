package com.example.mixtapp.data.model

// Lo mas largo que puede ser la opinion de un voto
const val MAX_OPINION = 140

// Se puede votar cuando ya elegi uno de los dos o, si ya vote, para cambiar solo la opinion
fun puedeVotar(opcionElegida: String?, miOpcion: String?): Boolean =
    opcionElegida != null || miOpcion != null
