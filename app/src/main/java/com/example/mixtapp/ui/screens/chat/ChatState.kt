package com.example.mixtapp.ui.screens.chat

import androidx.annotation.StringRes
import com.example.mixtapp.data.model.MensajeUi
import com.example.mixtapp.data.model.UsuarioDelChatUi

data class ChatState(
    // La otra persona; null mientras llega su perfil
    val otro: UsuarioDelChatUi? = null,
    // Los mensajes en vivo, el mas viejo primero
    val mensajes: List<MensajeUi> = emptyList(),
    val texto: String = "",
    val enviando: Boolean = false,
    // Arranca cargando porque lo primero que hace la pantalla es pedir el perfil
    val isLoading: Boolean = true,
    @StringRes val errorMessageRes: Int? = null,
    // No se pudo enviar o el mensaje esta vacio: la conversacion se sigue viendo
    @StringRes val errorEnviarRes: Int? = null,
)
