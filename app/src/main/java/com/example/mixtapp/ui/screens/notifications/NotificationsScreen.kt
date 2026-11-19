package com.example.mixtapp.ui.screens.notifications

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.data.model.fechaDeSalida
import com.example.mixtapp.ui.screens.notifications.components.DiscusionRow
import com.example.mixtapp.ui.screens.notifications.components.NotificationRow
import com.example.mixtapp.ui.screens.notifications.model.TAB_DISCUSIONES
import com.example.mixtapp.ui.screens.notifications.model.TAB_LANZAMIENTOS
import com.example.mixtapp.ui.screens.notifications.model.TAB_MIAS
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.FilaDeOpciones
import com.example.mixtapp.ui.utils.ItemRow
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.OpcionUi
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.SectionTitle
import com.example.mixtapp.ui.utils.TonoDeFondo

@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel,
    // A donde lleva cada notificacion lo decide AppNavigation
    onNotificationClick: (NotificationUi) -> Unit,
    onReviewClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onItemClick: (tipo: String, itemId: String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by notificationsViewModel.uiState.collectAsState()

    NotificationsScreenContent(
        hoy = state.hoy,
        antes = state.antes,
        discusiones = state.discusiones,
        lanzamientos = state.lanzamientos,
        tabs = state.tabs,
        selectedTabId = state.selectedTabId,
        isLoading = state.isLoading,
        cargandoDiscusiones = state.cargandoDiscusiones,
        cargandoLanzamientos = state.cargandoLanzamientos,
        errorMessageRes = state.errorMessageRes,
        errorDiscusionesRes = state.errorDiscusionesRes,
        errorLanzamientosRes = state.errorLanzamientosRes,
        errorAccionRes = state.errorAccionRes,
        onTabSelected = { notificationsViewModel.selectTab(id = it) },
        onNotificationClick = { notificacion ->
            // Se marca como leida y se abre lo que corresponda
            notificationsViewModel.abrirNotificacion(notificacion = notificacion)
            onNotificationClick(notificacion)
        },
        onReviewClick = onReviewClick,
        onUserClick = onUserClick,
        onItemClick = { item -> onItemClick(item.tipo, item.id) },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun NotificationsScreenContent(
    hoy: List<NotificationUi>,
    antes: List<NotificationUi>,
    discusiones: List<ReviewUi>,
    lanzamientos: List<ItemUi>,
    tabs: List<OpcionUi>,
    selectedTabId: String,
    isLoading: Boolean,
    cargandoDiscusiones: Boolean,
    cargandoLanzamientos: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorDiscusionesRes: Int?,
    @StringRes errorLanzamientosRes: Int?,
    @StringRes errorAccionRes: Int?,
    onTabSelected: (String) -> Unit,
    onNotificationClick: (NotificationUi) -> Unit,
    onReviewClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onItemClick: (ItemUi) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize().testTag("notificationsScreen")) {
        AppBackground(tono = TonoDeFondo.Social)

        Column(modifier = Modifier.fillMaxSize()) {
            // El titulo queda fijo para poder volver aunque falle la carga
            ScreenTitle(
                title = stringResource(R.string.notifications_title),
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            // Las pestanas tambien quedan fijas: cada una tiene su carga y su error, asi que si una
            // falla se puede pasar a las otras
            FilaDeOpciones(
                opciones = tabs,
                elegidaId = selectedTabId,
                onOpcionClick = onTabSelected,
                prefijoTestTag = "tab",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            )

            when (selectedTabId) {
                TAB_DISCUSIONES -> ListaDePestana(
                    cargando = cargandoDiscusiones,
                    vacia = discusiones.isEmpty(),
                    errorMessageRes = errorDiscusionesRes,
                    vaciaRes = R.string.sin_actividad_seguidos,
                    modifier = Modifier.weight(1f),
                ) {
                    items(discusiones, key = { it.id }) { review ->
                        DiscusionRow(
                            review = review,
                            onClick = { onReviewClick(review.id) },
                            onAuthorClick = { onUserClick(review.authorId) },
                        )
                    }
                }

                // Vacia si todavia no me gusta ningun artista o si no tienen albumes ni canciones
                TAB_LANZAMIENTOS -> ListaDePestana(
                    cargando = cargandoLanzamientos,
                    vacia = lanzamientos.isEmpty(),
                    errorMessageRes = errorLanzamientosRes,
                    vaciaRes = R.string.sin_lanzamientos,
                    modifier = Modifier.weight(1f),
                ) {
                    items(lanzamientos) { item ->
                        // Los lanzamientos llevan su fecha de salida al lado de la etiqueta
                        ItemRow(
                            item = item,
                            onClick = { onItemClick(item) },
                            modifier = Modifier.testTag("lanzamiento_${item.id}"),
                        ) {
                            Text(
                                text = item.fechaDeSalida,
                                color = MaterialTheme.colorScheme.primary,
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                }

                // TAB_MIAS: las de hoy y las de antes, cada grupo con su titulo
                else -> ListaDePestana(
                    cargando = isLoading,
                    vacia = hoy.isEmpty() && antes.isEmpty(),
                    errorMessageRes = errorMessageRes,
                    vaciaRes = R.string.sin_notificaciones,
                    modifier = Modifier.weight(1f),
                ) {
                    // Fallo marcar una como leida: va aparte del error de la lista
                    item { ErrorMessage(messageRes = errorAccionRes) }

                    grupoDeNotificaciones(
                        tituloRes = R.string.notificaciones_hoy,
                        notificaciones = hoy,
                        onNotificationClick = onNotificationClick,
                    )
                    grupoDeNotificaciones(
                        tituloRes = R.string.notificaciones_antes,
                        notificaciones = antes,
                        onNotificationClick = onNotificationClick,
                    )
                }
            }
        }
    }
}

// La lista de una pestana: la carga, el error si no hay nada cargado, o la lista con el aviso del
// error arriba (si el listener falla despues de cargar, queda lo de antes), el mensaje si esta vacia
// y lo que muestra cada pestana
@Composable
private fun ListaDePestana(
    cargando: Boolean,
    vacia: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes vaciaRes: Int,
    modifier: Modifier = Modifier,
    contenido: LazyListScope.() -> Unit,
) {
    when {
        cargando -> LoadingBox()
        vacia && errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
        else -> LazyColumn(
            modifier = modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item { ErrorMessage(messageRes = errorMessageRes) }

            if (vacia) {
                item {
                    EmptyMessage(messageRes = vaciaRes, modifier = Modifier.fillMaxWidth())
                }
            }

            contenido()
        }
    }
}

// Un grupo de mis notificaciones (hoy o antes) con su titulo; si no tiene ninguna no se muestra
private fun LazyListScope.grupoDeNotificaciones(
    @StringRes tituloRes: Int,
    notificaciones: List<NotificationUi>,
    onNotificationClick: (NotificationUi) -> Unit,
) {
    if (notificaciones.isEmpty()) return

    item(key = "seccion_$tituloRes") {
        SectionTitle(
            title = stringResource(tituloRes),
            actionText = null,
            onActionClick = null,
        )
    }

    items(notificaciones, key = { it.id }) { notificacion ->
        NotificationRow(
            notification = notificacion,
            onClick = { onNotificationClick(notificacion) },
        )
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun NotificationsScreenPreview() {
    val todas = LocalPreviewProvider.notifications

    MixtappTheme(darkTheme = true) {
        NotificationsScreenContent(
            hoy = todas.filter { it.isToday },
            antes = todas.filter { !it.isToday },
            discusiones = LocalPreviewProvider.following.reviews,
            lanzamientos = listOf(LocalPreviewProvider.karolG, LocalPreviewProvider.fromZero, LocalPreviewProvider.emptinessMachine),
            tabs = notificationTabs,
            selectedTabId = TAB_MIAS,
            isLoading = false,
            cargandoDiscusiones = false,
            cargandoLanzamientos = false,
            errorMessageRes = null,
            errorDiscusionesRes = null,
            errorLanzamientosRes = null,
            errorAccionRes = null,
            onTabSelected = {},
            onNotificationClick = {},
            onReviewClick = {},
            onUserClick = {},
            onItemClick = {},
            onBackClick = {},
        )
    }
}
