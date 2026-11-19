package com.example.mixtapp.data.model

// Con menos de esto entre reseñas y me gusta, Tu año no afirma cual es mi genero ni mi artista:
// todavia hay muy poco para concluirlo
const val MINIMO_PARA_CONCLUIR = 3

// true si el resumen tiene muy pocos datos para hablar de mis gustos
fun hayPocosDatos(resenas: Int, meGusta: Int): Boolean = resenas + meGusta < MINIMO_PARA_CONCLUIR
