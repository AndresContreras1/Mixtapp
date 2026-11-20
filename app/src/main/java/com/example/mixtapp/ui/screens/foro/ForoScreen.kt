package com.example.mixtapp.ui.screens.foro

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
import androidx.compose.foundation.layout.size
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
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.MensajeUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.BackButton
import com.example.mixtapp.ui.utils.BurbujaDeMensaje
import com.example.mixtapp.ui.utils.CampoDeMensaje
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.TonoDeFondo

@Composable
fun ForoScreen(
    tipo: String,
    itemId: String,
    foroViewModel: ForoViewModel,
    onAuthorClick: (String) -> Unit,
    onItemClick: (tipo: String, itemId: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by foroViewModel.uiState.collectAsState()

    // Necesita el tipo y el id que llegan por la ruta
    LaunchedEffect(Unit) {
        foroViewModel.abrirForo(tipo = tipo, itemId = itemId)
    }

    ForoScreenContent(
        isLoading = state.isLoading,
        item = state.item,
        mensajes = state.mensajes,
        texto = state.texto,
        enviando = state.enviando,
        errorMessageRes = state.errorMessageRes,
        errorEnviarRes = state.errorEnviarRes,
        onTextoChange = { foroViewModel.updateTexto(texto = it) },
        onEnviarClick = { foroViewModel.enviar() },
        onAuthorClick = onAuthorClick,
        onItemClick = { item -> onItemClick(item.tipo, item.id) },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

// El foro: arriba el item (tocarlo abre su detalle), en el medio los mensajes como un chat y abajo el
// campo para escribir, que sube con el teclado
@Composable
fun ForoScreenContent(
    isLoading: Boolean,
    item: ItemUi?,
    mensajes: List<MensajeUi>,
    texto: String,
    enviando: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorEnviarRes: Int?,
    onTextoChange: (String) -> Unit,
    onEnviarClick: () -> Unit,
    onAuthorClick: (String) -> Unit,
    onItemClick: (ItemUi) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().testTag("foroScreen")) {
        AppBackground(tono = TonoDeFondo.Social)

        when {
            isLoading -> LoadingBox()
            item == null -> ErrorBox(messageRes = errorMessageRes ?: R.string.item_no_encontrado)
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

                    ItemAsyncImage(
                        imagenUrl = item.imagenUrl,
                        titulo = item.titulo,
                        esquinas = 10.dp,
                        modifier = Modifier.size(44.dp),
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.foro_de, item.titulo),
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleMedium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = stringResource(R.string.foro_descripcion),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }

                    Text(
                        text = stringResource(R.string.ver_detalle),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clickable { onItemClick(item) },
                    )
                }

                ErrorMessage(messageRes = errorMessageRes, modifier = Modifier.padding(horizontal = 20.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .testTag("mensajesForo"),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    if (mensajes.isEmpty()) {
                        item {
                            EmptyMessage(messageRes = R.string.sin_mensajes_foro, modifier = Modifier.fillMaxWidth())
                        }
                    }
                    items(mensajes, key = { it.id }) { mensaje ->
                        BurbujaDeMensaje(mensaje = mensaje, mostrarAutor = true, onAutorClick = onAuthorClick)
                    }
                }

                Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)) {
                    ErrorMessage(messageRes = errorEnviarRes)
                    CampoDeMensaje(
                        value = texto,
                        placeholder = stringResource(R.string.escribe_al_foro),
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
fun ForoScreenPreview() {
    MixtappTheme(darkTheme = true) {
        ForoScreenContent(
            isLoading = false,
            item = LocalPreviewProvider.fromZero,
            mensajes = listOf(
                MensajeUi("1", "u1", "Laura", "", "Emily le dio otro aire a la banda", "2 h", esMio = false),
                MensajeUi("2", "yo", "Yo", "", "«The Emptiness Machine» no me sale de la cabeza", "", esMio = true),
            ),
            texto = "",
            enviando = false,
            errorMessageRes = null,
            errorEnviarRes = null,
            onTextoChange = {},
            onEnviarClick = {},
            onAuthorClick = {},
            onItemClick = {},
            onBackClick = {},
        )
    }
}
