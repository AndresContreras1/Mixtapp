package com.example.mixtapp.data.repository

import com.google.firebase.firestore.FirebaseFirestoreException
import retrofit2.HttpException

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

class ErrorDelServidorException : Exception("La base remota respondio con un error")

// Lo que no tiene un catch propio; el detalle de la excepcion original queda en el log
class ErrorInesperadoException : Exception("Fallo inesperado")

// El backend responde 404 cuando el album, el usuario o la resena no existen
fun errorDelBackend(e: HttpException): Exception =
    if (e.code() == 404) ContenidoNoEncontradoException() else ErrorDelServidorException()

// Firestore dice por que fallo con un codigo: UNAVAILABLE es que no hay conexion; lo demas, como
// los permisos de las reglas, se muestra como un error del servidor
fun errorDeFirestore(e: FirebaseFirestoreException): Exception =
    if (e.code == FirebaseFirestoreException.Code.UNAVAILABLE) SinConexionException() else ErrorDelServidorException()

// Los datos en vivo no retornan Result: el repositorio cambia la excepcion por la de la app en el
// catch del flujo y el ViewModel la recibe en el suyo
fun errorDelFlujo(e: Throwable): Exception = when (e) {
    is HttpException -> errorDelBackend(e)
    is FirebaseFirestoreException -> errorDeFirestore(e)
    is NoSuchElementException -> ContenidoNoEncontradoException()
    else -> ErrorInesperadoException()
}
