package com.example.mixtapp.data.repository

// El mensaje de la excepcion es para el log del programador. El texto que ve el
// usuario lo elige el ViewModel desde strings.xml

class CredencialesInvalidasException : Exception("Correo o contrasena incorrectos")

class CorreoNoRegistradoException : Exception("No existe una cuenta con ese correo")

class CorreoYaRegistradoException : Exception("Ese correo ya tiene una cuenta")

class SinConexionException : Exception("No hay conexion con la red")

class DemasiadosIntentosException : Exception("Demasiados intentos seguidos")

class SinSesionException : Exception("No hay una sesion iniciada")

class ErrorDeInicioSesionException : Exception("Fallo el inicio de sesion")

class ErrorDeRegistroException : Exception("Fallo el registro")

class CuotaExcedidaException : Exception("Se agoto la cuota de Storage")

class PermisoDenegadoException : Exception("Las reglas de Storage no permiten la operacion")

class ErrorAlSubirImagenException : Exception("Fallo la subida de la imagen")

class ContenidoNoEncontradoException : Exception("No se encontro el contenido")

class ErrorDeDatosLocalesException : Exception("Fallo la lectura de los datos locales")
