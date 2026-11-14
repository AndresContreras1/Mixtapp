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
        AppBackground()

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
                TAB_DISCUSIONES -> when {
                    cargandoDiscusiones -> LoadingBox()
                    // Sin discusiones cargadas solo se muestra el motivo del error
                    discusiones.isEmpty() && errorDiscusionesRes != null -> ErrorBox(messageRes = errorDiscusionesRes)
                    else -> LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        // Si el listener falla despues de cargar, el aviso sale arriba y queda lo de antes
                        item { ErrorMessage(messageRes = errorDiscusionesRes) }
                        if (discusiones.isEmpty()) {
                            item {
                                EmptyMessage(
                                    messageRes = R.string.sin_actividad_seguidos,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        items(discusiones, key = { it.id }) { review ->
                            DiscusionRow(
                                review = review,
                                onClick = { onReviewClick(review.id) },
                                onAuthorClick = { onUserClick(review.authorId) },
                            )
                        }
                    }
                }

                TAB_LANZAMIENTOS -> when {
                    cargandoLanzamientos -> LoadingBox()
                    // Sin lanzamientos cargados solo se muestra el motivo del error
                    lanzamientos.isEmpty() && errorLanzamientosRes != null -> ErrorBox(messageRes = errorLanzamientosRes)
                    else -> LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        item { ErrorMessage(messageRes = errorLanzamientosRes) }
                        // Vacia si todavia no me gusta ningun artista o si no tienen albumes ni canciones
                        if (lanzamientos.isEmpty()) {
                            item {
                                EmptyMessage(
                                    messageRes = R.string.sin_lanzamientos,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        items(lanzamientos) { item ->
                            // Los lanzamientos llevan su anio al lado de la etiqueta
                            ItemRow(
                                item = item,
                                onClick = { onItemClick(item) },
                                modifier = Modifier.testTag("lanzamiento_${item.id}"),
                            ) {
                                Text(
                                    text = item.anio,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        }
                    }
                }

                // TAB_MIAS
                else -> when {
                    isLoading -> LoadingBox()
                    // Sin notificaciones cargadas solo se muestra el motivo del error
                    hoy.isEmpty() && antes.isEmpty() && errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
                    else -> {
                        ErrorMessage(
                            messageRes = errorMessageRes,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        ErrorMessage(
                            messageRes = errorAccionRes,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            if (hoy.isEmpty() && antes.isEmpty()) {
                                item {
                                    EmptyMessage(
                                        messageRes = R.string.sin_notificaciones,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            if (hoy.isNotEmpty()) {
                                item(key = "seccion_hoy") {
                                    SectionTitle(
                                        title = stringResource(R.string.notificaciones_hoy),
                                        actionText = null,
                                        onActionClick = null,
                                    )
                                }

                                items(hoy, key = { it.id }) { notificacion ->
                                    NotificationRow(
                                        notification = notificacion,
                                        onClick = { onNotificationClick(notificacion) },
                                    )
                                }
                            }

                            if (antes.isNotEmpty()) {
                                item(key = "seccion_antes") {
                                    SectionTitle(
                                        title = stringResource(R.string.notificaciones_antes),
                                        actionText = null,
                                        onActionClick = null,
                                    )
                                }

                                items(antes, key = { it.id }) { notificacion ->
                                    NotificationRow(
                                        notification = notificacion,
                                        onClick = { onNotificationClick(notificacion) },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
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
