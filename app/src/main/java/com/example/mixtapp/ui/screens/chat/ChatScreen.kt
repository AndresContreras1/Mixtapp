package com.example.mixtapp.ui.screens.chat

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.MensajeUi
import com.example.mixtapp.data.model.UsuarioDelChatUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.BackButton
import com.example.mixtapp.ui.utils.BurbujaDeMensaje
import com.example.mixtapp.ui.utils.CampoDeMensaje
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ProfileAsyncImage
import com.example.mixtapp.ui.utils.TonoDeFondo

@Composable
fun ChatScreen(
    otroId: String,
    chatViewModel: ChatViewModel,
    onPerfilClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by chatViewModel.uiState.collectAsState()

    // Necesita el id de la otra persona, que llega por la ruta
    LaunchedEffect(Unit) {
        chatViewModel.abrirChat(otroId = otroId)
    }

    ChatScreenContent(
        isLoading = state.isLoading,
        otro = state.otro,
        mensajes = state.mensajes,
        texto = state.texto,
        enviando = state.enviando,
        errorMessageRes = state.errorMessageRes,
        errorEnviarRes = state.errorEnviarRes,
        onTextoChange = { chatViewModel.updateTexto(texto = it) },
        onEnviarClick = { chatViewModel.enviar() },
        onPerfilClick = onPerfilClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

// El chat: arriba la otra persona (tocarla abre su perfil), los mensajes y el campo para escribir
@Composable
fun ChatScreenContent(
    isLoading: Boolean,
    otro: UsuarioDelChatUi?,
    mensajes: List<MensajeUi>,
    texto: String,
    enviando: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorEnviarRes: Int?,
    onTextoChange: (String) -> Unit,
    onEnviarClick: () -> Unit,
    onPerfilClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().testTag("chatScreen")) {
        AppBackground(tono = TonoDeFondo.Social)

        when {
            isLoading -> LoadingBox()
            otro == null -> ErrorBox(messageRes = errorMessageRes ?: R.string.error_cargar_contenido)
            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding(),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    BackButton(onBackClick = onBackClick)

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(
                        modifier = Modifier.clickable { onPerfilClick(otro.id) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        ProfileAsyncImage(
                            profileImage = otro.fotoUrl,
                            contentDescription = stringResource(R.string.foto_perfil),
                            size = 40,
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = otro.nombre,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                ErrorMessage(messageRes = errorMessageRes, modifier = Modifier.padding(horizontal = 20.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("mensajesChat"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (mensajes.isEmpty()) {
                        item {
                            EmptyMessage(messageRes = R.string.sin_mensajes_chat, modifier = Modifier.fillMaxWidth())
                        }
                    }
                    items(mensajes, key = { it.id }) { mensaje ->
                        BurbujaDeMensaje(mensaje = mensaje, mostrarAutor = false, onAutorClick = null)
                    }
                }

                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                    ErrorMessage(messageRes = errorEnviarRes)
                    CampoDeMensaje(
                        value = texto,
                        placeholder = stringResource(R.string.escribe_un_mensaje),
                        enviando = enviando,
                        onValueChange = onTextoChange,
                        onEnviarClick = onEnviarClick,
                    )
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun ChatScreenPreview() {
    MixtappTheme(darkTheme = true) {
        ChatScreenContent(
            isLoading = false,
            otro = UsuarioDelChatUi(id = "u1", nombre = "Laura Gómez", fotoUrl = ""),
            mensajes = listOf(
                MensajeUi("1", "u1", "Laura", "", "Vi que también te gusta Deftones 🤘", "5 min", esMio = false),
                MensajeUi("2", "yo", "Yo", "", "¡White Pony es de mis favoritos!", "", esMio = true),
            ),
            texto = "",
            enviando = false,
            errorMessageRes = null,
            errorEnviarRes = null,
            onTextoChange = {},
            onEnviarClick = {},
            onPerfilClick = {},
            onBackClick = {},
        )
    }
}
