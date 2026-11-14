package com.example.mixtapp.data.model

// Las reglas de la cuenta: las mismas al iniciar sesion y al registrarse

// La contrasena tiene minimo 6 caracteres, como en clase y en la politica de Authentication
const val MIN_PASSWORD_LENGTH = 6

// El correo tiene que tener una arroba; lo demas lo revisa Authentication
fun esCorreoValido(correo: String): Boolean = correo.contains("@")

// Una cuenta sin documento en usuarios usa como usuario la parte del correo antes de la arroba
fun usuarioDelCorreo(correo: String): String = correo.substringBefore("@")
