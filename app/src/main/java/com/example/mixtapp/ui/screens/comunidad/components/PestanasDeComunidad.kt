package com.example.mixtapp.ui.screens.comunidad.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.AfinidadUi
import com.example.mixtapp.data.model.ChatUi
import com.example.mixtapp.data.model.ForoUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.EspacioDelFab
import com.example.mixtapp.ui.utils.ItemAsyncImage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ProfileAsyncImage
import com.example.mixtapp.ui.utils.SectionTitle
import com.example.mixtapp.ui.utils.rebote
import com.example.mixtapp.ui.utils.texturaDeTarjeta

// ---------------------------------------------------------------- Afinidad

// La pestana Afinidad: las personas con gustos parecidos a los mios, la de mas match primero
@Composable
fun PestanaAfinidad(
    afinidades: List<AfinidadUi>,
    cargando: Boolean,
    @StringRes errorRes: Int?,
    onUsuarioClick: (String) -> Unit,
    onMensajeClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListaDeComunidad(
        cargando = cargando,
        vacia = afinidades.isEmpty(),
        errorRes = errorRes,
        vaciaRes = R.string.sin_afinidad,
        modifier = modifier,
    ) {
        items(afinidades, key = { it.usuario.id }) { afinidad ->
            AfinidadRow(
                afinidad = afinidad,
                onClick = { onUsuarioClick(afinidad.usuario.id) },
                onMensajeClick = { onMensajeClick(afinidad.usuario.id) },
            )
        }
    }
}

// Una persona con su foto, su nombre, el porcentaje de match en rosado, el genero que comparten y
// las portadas de lo que les gusta a los dos. El globo abre el chat con ella
@Composable
fun AfinidadRow(
    afinidad: AfinidadUi,
    onClick: () -> Unit,
    onMensajeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .rebote()
            .texturaDeTarjeta(RoundedCornerShape(22.dp))
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("afinidad_${afinidad.usuario.id}"),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAsyncImage(
                profileImage = afinidad.usuario.fotoUrl,
                contentDescription = stringResource(R.string.foto_perfil),
                size = 52,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = afinidad.usuario.nombre,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (afinidad.generoEnComun != null) {
                    Text(
                        text = stringResource(R.string.genero_en_comun, afinidad.generoEnComun),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }

            // El porcentaje, en una pastilla rosada
            Text(
                text = stringResource(R.string.porcentaje_match, afinidad.porcentaje),
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 10.dp, vertical = 4.dp),
            )

            IconButton(onClick = onMensajeClick, modifier = Modifier.testTag("mensaje_${afinidad.usuario.id}")) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.Chat,
                    contentDescription = stringResource(R.string.enviar_mensaje),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
        }

        if (afinidad.enComun.isNotEmpty()) {
            Text(
                text = stringResource(R.string.les_gusta_a_los_dos),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.padding(top = 10.dp, bottom = 6.dp),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                afinidad.enComun.forEach { item ->
                    ItemAsyncImage(
                        imagenUrl = item.imagenUrl,
                        titulo = item.titulo,
                        esquinas = 10.dp,
                        modifier = Modifier.size(52.dp),
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------- Foros

// La pestana Foros: arriba los foros con conversacion y abajo los de lo que me gusta, para empezar uno
@Composable
fun PestanaForos(
    foros: List<ForoUi>,
    sugeridos: List<ItemResumenUi>,
    cargando: Boolean,
    @StringRes errorRes: Int?,
    onForoClick: (ItemResumenUi) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListaDeComunidad(
        cargando = cargando,
        vacia = foros.isEmpty() && sugeridos.isEmpty(),
        errorRes = errorRes,
        vaciaRes = R.string.sin_foros,
        modifier = modifier,
    ) {
        if (foros.isNotEmpty()) {
            item {
                SectionTitle(title = stringResource(R.string.foros_activos), actionText = null, onActionClick = null)
            }
            items(foros, key = { it.item.id }) { foro ->
                ForoRow(foro = foro, onClick = { onForoClick(foro.item) })
            }
        }

        if (sugeridos.isNotEmpty()) {
            item {
                SectionTitle(
                    title = stringResource(R.string.foros_de_lo_que_te_gusta),
                    modifier = Modifier.padding(top = 8.dp),
                    actionText = null,
                    onActionClick = null,
                )
            }
            items(sugeridos, key = { "sugerido_${it.id}" }) { item ->
                ForoSugeridoRow(item = item, onClick = { onForoClick(item) })
            }
        }
    }
}

// Un foro con conversacion: la portada, el titulo, cuantos mensajes y hace cuanto fue el ultimo
@Composable
fun ForoRow(
    foro: ForoUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .rebote()
            .texturaDeTarjeta(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("foro_${foro.item.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemAsyncImage(imagenUrl = foro.item.imagenUrl, titulo = foro.item.titulo, esquinas = 12.dp, modifier = Modifier.size(56.dp))

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = foro.item.titulo,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = foro.item.subtitulo,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(R.string.mensajes_count, foro.mensajes) +
                    if (foro.timeAgo.isNotEmpty()) "  ·  ${foro.timeAgo}" else "",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelMedium,
            )
        }

        Icon(
            imageVector = Icons.Outlined.Forum,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

// Algo que me gusta y todavia no tiene foro: la misma fila de las listas, con "Entrar al foro"
@Composable
fun ForoSugeridoRow(
    item: ItemResumenUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .rebote()
            .texturaDeTarjeta(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ItemAsyncImage(imagenUrl = item.imagenUrl, titulo = item.titulo, esquinas = 12.dp, modifier = Modifier.size(48.dp))

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = item.titulo,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = stringResource(R.string.foro_de_esto),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

// ---------------------------------------------------------------- Mensajes

// La pestana Mensajes: mis conversaciones, la ultima primero
@Composable
fun PestanaMensajes(
    chats: List<ChatUi>,
    cargando: Boolean,
    @StringRes errorRes: Int?,
    onChatClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    ListaDeComunidad(
        cargando = cargando,
        vacia = chats.isEmpty(),
        errorRes = errorRes,
        vaciaRes = R.string.sin_chats,
        modifier = modifier,
    ) {
        items(chats, key = { it.id }) { chat ->
            ChatRow(chat = chat, onClick = { onChatClick(chat.otro.id) })
        }
    }
}

// Una conversacion: la foto y el nombre de la otra persona, el ultimo mensaje y hace cuanto
@Composable
fun ChatRow(
    chat: ChatUi,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .rebote()
            .texturaDeTarjeta(RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("chat_${chat.otro.id}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ProfileAsyncImage(profileImage = chat.otro.fotoUrl, contentDescription = stringResource(R.string.foto_perfil), size = 48)

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = chat.otro.nombre,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = chat.ultimoMensaje,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        Text(
            text = chat.timeAgo.ifEmpty { stringResource(R.string.ahora) },
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

// ---------------------------------------------------------------- Comun

// La lista de una pestana con su carga, su error y su mensaje vacio, como las de las notificaciones
@Composable
private fun ListaDeComunidad(
    cargando: Boolean,
    vacia: Boolean,
    @StringRes errorRes: Int?,
    @StringRes vaciaRes: Int,
    modifier: Modifier = Modifier,
    elementos: LazyListScope.() -> Unit,
) {
    when {
        cargando && vacia -> LoadingBox()
        else -> LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = EspacioDelFab),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item { ErrorMessage(messageRes = errorRes) }
            if (vacia && errorRes == null) {
                item { EmptyMessage(messageRes = vaciaRes, modifier = Modifier.fillMaxWidth()) }
            }
            elementos()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AfinidadRowPreview() {
    MixtappTheme(darkTheme = true) {
        AfinidadRow(
            afinidad = AfinidadUi(
                usuario = LocalPreviewProvider.otroUsuario,
                porcentaje = 87,
                enComun = LocalPreviewProvider.favoritos.take(3),
                generoEnComun = "Nu metal",
            ),
            onClick = {},
            onMensajeClick = {},
        )
    }
}
