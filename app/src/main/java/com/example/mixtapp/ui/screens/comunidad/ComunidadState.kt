package com.example.mixtapp.ui.screens.comunidad

import androidx.annotation.StringRes
import com.example.mixtapp.R
import com.example.mixtapp.data.model.AfinidadUi
import com.example.mixtapp.data.model.ChatUi
import com.example.mixtapp.data.model.ForoUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.ui.utils.OpcionUi

const val PESTANA_SIGUIENDO = "siguiendo"
const val PESTANA_AFINIDAD = "afinidad"
const val PESTANA_FOROS = "foros"
const val PESTANA_MENSAJES = "mensajes"

// Las cuatro pestanas de Comunidad, en el orden en que se ven
val comunidadTabs = listOf(
    OpcionUi(id = PESTANA_SIGUIENDO, label = R.string.tab_siguiendo),
    OpcionUi(id = PESTANA_AFINIDAD, label = R.string.tab_afinidad),
    OpcionUi(id = PESTANA_FOROS, label = R.string.tab_foros),
    OpcionUi(id = PESTANA_MENSAJES, label = R.string.tab_mensajes),
)

data class ComunidadState(
    val tabs: List<OpcionUi> = comunidadTabs,
    val selectedTabId: String = PESTANA_SIGUIENDO,
    // Afinidad: se calcula una vez al abrir Comunidad
    val afinidades: List<AfinidadUi> = emptyList(),
    val cargandoAfinidad: Boolean = false,
    @StringRes val errorAfinidadRes: Int? = null,
    // Foros: los que tienen conversacion, en vivo, y los de lo que me gusta que todavia no la tienen
    val foros: List<ForoUi> = emptyList(),
    val forosSugeridos: List<ItemResumenUi> = emptyList(),
    val cargandoForos: Boolean = false,
    @StringRes val errorForosRes: Int? = null,
    // Mensajes: mis chats, en vivo
    val chats: List<ChatUi> = emptyList(),
    val cargandoChats: Boolean = false,
    @StringRes val errorChatsRes: Int? = null,
    // Con esto el ViewModel arma los foros sugeridos
    val favoritos: List<ItemResumenUi> = emptyList(),
)
