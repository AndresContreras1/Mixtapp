package com.example.mixtapp.ui.screens.comunidad

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.comunidad.components.PestanaAfinidad
import com.example.mixtapp.ui.screens.comunidad.components.PestanaForos
import com.example.mixtapp.ui.screens.comunidad.components.PestanaMensajes
import com.example.mixtapp.ui.screens.following.FollowingScreen
import com.example.mixtapp.ui.screens.following.FollowingViewModel
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.FilaDeOpciones
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.TonoDeFondo

// Comunidad, lo que antes era Siguiendo: el titulo y cuatro pestanas. Siguiendo mantiene su pantalla
// y su ViewModel; Afinidad, Foros y Mensajes salen de ComunidadViewModel
@Composable
fun ComunidadScreen(
    comunidadViewModel: ComunidadViewModel,
    followingViewModel: FollowingViewModel,
    onAuthorClick: (String) -> Unit,
    onItemClick: (String, String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onForoClick: (tipo: String, itemId: String) -> Unit,
    onChatClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by comunidadViewModel.uiState.collectAsState()

    Box(modifier = modifier.fillMaxSize().testTag("comunidadScreen")) {
        AppBackground(tono = TonoDeFondo.Social)

        Column(modifier = Modifier.fillMaxSize()) {
            // El titulo y las pestanas quedan fijos: si una pestana falla se puede pasar a otra
            ScreenTitle(
                title = stringResource(R.string.comunidad_titulo),
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            FilaDeOpciones(
                opciones = state.tabs,
                elegidaId = state.selectedTabId,
                onOpcionClick = { comunidadViewModel.selectTab(id = it) },
                prefijoTestTag = "comunidad",
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 12.dp),
            )

            val cuerpo = Modifier
                .fillMaxWidth()
                .weight(1f)

            when (state.selectedTabId) {
                PESTANA_AFINIDAD -> PestanaAfinidad(
                    afinidades = state.afinidades,
                    cargando = state.cargandoAfinidad,
                    errorRes = state.errorAfinidadRes,
                    onUsuarioClick = onAuthorClick,
                    onMensajeClick = onChatClick,
                    modifier = cuerpo,
                )
                PESTANA_FOROS -> PestanaForos(
                    foros = state.foros,
                    sugeridos = state.forosSugeridos,
                    cargando = state.cargandoForos,
                    errorRes = state.errorForosRes,
                    onForoClick = { item -> onForoClick(item.tipo, item.id) },
                    modifier = cuerpo,
                )
                PESTANA_MENSAJES -> PestanaMensajes(
                    chats = state.chats,
                    cargando = state.cargandoChats,
                    errorRes = state.errorChatsRes,
                    onChatClick = onChatClick,
                    modifier = cuerpo,
                )
                // PESTANA_SIGUIENDO
                else -> FollowingScreen(
                    followingViewModel = followingViewModel,
                    onAuthorClick = onAuthorClick,
                    onItemClick = onItemClick,
                    onCommentsClick = onCommentsClick,
                    modifier = cuerpo,
                )
            }
        }
    }
}
