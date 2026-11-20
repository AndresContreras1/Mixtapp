package com.example.mixtapp.ui.screens.foro

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MensajeUi

data class ForoState(
    // El item del foro; null mientras carga o si no existe
    val item: ItemUi? = null,
    // Los mensajes en vivo, el mas viejo primero, como un chat
    val mensajes: List<MensajeUi> = emptyList(),
    val texto: String = "",
    val enviando: Boolean = false,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el item
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    // No se pudo enviar o el mensaje esta vacio: el foro se sigue viendo
    @StringRes val errorEnviarRes: Int? = null,
)
