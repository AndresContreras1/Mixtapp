package com.example.mixtapp.ui.utils

import androidx.annotation.StringRes
import com.example.mixtapp.R
import com.example.mixtapp.data.repository.ContenidoNoEncontradoException
import com.example.mixtapp.data.repository.CorreoNoRegistradoException
import com.example.mixtapp.data.repository.CorreoYaRegistradoException
import com.example.mixtapp.data.repository.CredencialesInvalidasException
import com.example.mixtapp.data.repository.CuotaExcedidaException
import com.example.mixtapp.data.repository.DemasiadosIntentosException
import com.example.mixtapp.data.repository.ErrorDelServidorException
import com.example.mixtapp.data.repository.PermisoDenegadoException
import com.example.mixtapp.data.repository.SeguirseASiMismoException
import com.example.mixtapp.data.repository.SinConexionException
import com.example.mixtapp.data.repository.SinSesionException

// El texto que ve el usuario segun la excepcion que entrega el repositorio: un mensaje por cada
// error que el usuario puede provocar y el general de la pantalla para el resto. noEncontrado deja
// que cada pantalla diga que es lo que no existe (el album, la discusion, el perfil)
@StringRes
fun mensajeDeError(
    error: Throwable?,
    @StringRes generico: Int,
    @StringRes noEncontrado: Int = R.string.contenido_no_encontrado,
): Int = when (error) {
    is SinConexionException -> R.string.error_sin_conexion
    is ErrorDelServidorException -> R.string.error_servidor
    is SinSesionException -> R.string.error_sin_sesion
    is ContenidoNoEncontradoException -> noEncontrado
    is PermisoDenegadoException -> R.string.error_permiso_denegado
    is CuotaExcedidaException -> R.string.error_cuota_excedida
    is CredencialesInvalidasException -> R.string.error_credenciales
    is CorreoNoRegistradoException -> R.string.error_correo_no_registrado
    is CorreoYaRegistradoException -> R.string.error_correo_ya_registrado
    is DemasiadosIntentosException -> R.string.error_demasiados_intentos
    is SeguirseASiMismoException -> R.string.error_seguirse_a_si_mismo
    else -> generico
}
