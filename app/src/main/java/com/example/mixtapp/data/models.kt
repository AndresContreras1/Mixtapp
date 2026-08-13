package com.example.mixtapp.data

data class Usuario(
    val idUsuario: Int,
    val nombreUsuario: String,
    val correo: String,
    val fotoPerfilUrl: String = ""
)

data class Cancion(
    val idCancion: Int,
    val titulo: String,
    val artista: String,
    val genero: String,
    val calificacionMedia: Double = 0.0,
    val portadaUrl: String = ""
)

data class Resenia(
    val idResenia: Int,
    val nombreUsuario: String,
    val tituloObra: String,
    val comentario: String,
    val calificacion: Int
)