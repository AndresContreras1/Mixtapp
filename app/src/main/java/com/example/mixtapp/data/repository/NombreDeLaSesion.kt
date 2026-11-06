package com.example.mixtapp.data.repository

import com.example.mixtapp.data.datasource.AuthRemoteDataSource
import com.example.mixtapp.data.datasource.UsuarioRemoteDataSource

// El nombre que se copia en lo que escribe el usuario de la app (una resena, un comentario, un
// voto). Una cuenta sin documento en usuarios usa el usuario de su correo. Si Firestore falla por
// otra razon, la excepcion sigue y falla todo el guardado, como en clase
suspend fun nombreDeLaSesion(
    usuarioRemoteDataSource: UsuarioRemoteDataSource,
    authRemoteDataSource: AuthRemoteDataSource,
    usuarioId: String,
): String {
    val usuario = usuarioRemoteDataSource.getUsuarioById(usuarioId)

    return usuario?.toUsuarioUi()?.nombre
        ?: authRemoteDataSource.currentUser?.email?.substringBefore("@")
        ?: ""
}
