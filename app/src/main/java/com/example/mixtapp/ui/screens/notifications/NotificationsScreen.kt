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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.ui.screens.notifications.components.NotificationRow
import com.example.mixtapp.ui.screens.notifications.components.NotificationsTabs
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_SIN_LEER
import com.example.mixtapp.ui.screens.notifications.model.TAB_TODAS
import com.example.mixtapp.ui.screens.notifications.model.notificationTabs
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ScreenTitle
import com.example.mixtapp.ui.utils.SectionTitle

@Composable
fun NotificationsScreen(
    notificationsViewModel: NotificationsViewModel,
    onReviewClick: (String) -> Unit,
    onUserClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by notificationsViewModel.uiState.collectAsState()

    NotificationsScreenContent(
        hoy = state.hoy,
        antes = state.antes,
        tabs = state.tabs,
        unreadCount = state.unreadCount,
        selectedTabId = state.selectedTabId,
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        errorAccionRes = state.errorAccionRes,
        onTabSelected = { notificationsViewModel.selectTab(id = it) },
        onNotificationClick = { notificacion ->
            notificationsViewModel.abrirNotificacion(notificacion = notificacion)

            // Un like, un comentario o una respuesta abren la resena; un seguidor nuevo, su perfil
            val reviewId = notificacion.reviewId
            if (!reviewId.isNullOrEmpty()) {
                onReviewClick(reviewId)
            } else {
                onUserClick(notificacion.actorId)
            }
        },
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun NotificationsScreenContent(
    hoy: List<NotificationUi>,
    antes: List<NotificationUi>,
    tabs: List<NotificationTabUi>,
    unreadCount: Int,
    selectedTabId: String,
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorAccionRes: Int?,
    onTabSelected: (String) -> Unit,
    onNotificationClick: (NotificationUi) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // El titulo queda fijo para poder volver aunque falle la carga
            ScreenTitle(
                title = stringResource(R.string.notifications_title),
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            when {
                isLoading -> LoadingBox()
                // Sin notificaciones cargadas solo se muestra el motivo del error
                hoy.isEmpty() && antes.isEmpty() && errorMessageRes != null -> ErrorBox(messageRes = errorMessageRes)
                else -> {
                    NotificationsTabs(
                        tabs = tabs,
                        unreadCount = unreadCount,
                        selectedTabId = selectedTabId,
                        onTabSelected = onTabSelected,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
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
                                    // En Sin leer puede que si haya notificaciones, pero ya leidas
                                    messageRes = if (selectedTabId == TAB_SIN_LEER) {
                                        R.string.sin_notificaciones_sin_leer
                                    } else {
                                        R.string.sin_notificaciones
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        if (hoy.isNotEmpty()) {
                            item(key = "seccion_hoy") {
                                SectionTitle(title = stringResource(R.string.notificaciones_hoy))
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
                                SectionTitle(title = stringResource(R.string.notificaciones_antes))
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

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun NotificationsScreenPreview() {
    val todas = LocalPreviewProvider.notifications

    MixtappTheme(darkTheme = true) {
        NotificationsScreenContent(
            hoy = todas.filter { it.isToday },
            antes = todas.filter { !it.isToday },
            tabs = notificationTabs,
            unreadCount = todas.count { !it.isRead },
            selectedTabId = TAB_TODAS,
            isLoading = false,
            errorMessageRes = null,
            errorAccionRes = null,
            onTabSelected = {},
            onNotificationClick = {},
            onBackClick = {},
        )
    }
}
