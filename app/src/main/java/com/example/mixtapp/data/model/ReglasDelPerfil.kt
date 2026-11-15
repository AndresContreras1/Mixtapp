package com.example.mixtapp.data.model

// Limites de caracteres de cada campo del perfil, iguales al registrarse y al editarlo
const val MAX_NOMBRE_LENGTH = 40
const val MAX_USERNAME_LENGTH = 20
const val MAX_BIO_LENGTH = 150
// El limite de Instagram para el nombre de usuario
const val MAX_INSTAGRAM_LENGTH = 30

// El nombre de usuario va sin espacios: es el que se ve con la @ y el que se busca
fun esUsuarioValido(usuario: String): Boolean = !usuario.contains(" ")

// El Instagram es opcional; si se escribe, solo lleva letras sin tilde, numeros, puntos y guiones
// bajos, hasta el limite de Instagram
fun esInstagramValido(instagram: String): Boolean =
    instagram.length <= MAX_INSTAGRAM_LENGTH &&
        instagram.all { it in 'a'..'z' || it in 'A'..'Z' || it in '0'..'9' || it == '.' || it == '_' }

