package com.example.mixtapp.data.dtos

import com.example.mixtapp.data.model.UsuarioUi

// Cada base nombra distinto los campos del usuario, asi que cada una tiene su DTO.
// La clase padre no define campos: solo obliga a convertir el DTO al modelo de la pantalla
abstract class UsuarioDtoGeneric {
    abstract fun toUsuarioUi(): UsuarioUi
}
