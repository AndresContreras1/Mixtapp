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
import com.example.mixtapp.data.model.ItemUi
import com.example.mixtapp.data.model.NotificationUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.notifications.components.DiscusionRow
import com.example.mixtapp.ui.screens.notifications.components.LanzamientoRow
import com.example.mixtapp.ui.screens.notifications.components.NotificationRow
import com.example.mixtapp.ui.screens.notifications.components.NotificationsTabs
import com.example.mixtapp.ui.screens.notifications.model.NotificationTabUi
import com.example.mixtapp.ui.screens.notifications.model.TAB_DISCUSIONES
import com.example.mixtapp.ui.screens.notifications.model.TAB_LANZAMIENTOS
import com.example.mixtapp.ui.screens.notifications.model.TAB_MIAS
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
            notificationsViewModel.abrirNotificacion(notificacion = notificacion)

            // Un like (a la resena o a un comentario), un comentario o una respuesta abren la
            // discusion; un seguidor nuevo, su perfil
            val reviewId = notificacion.reviewId
            if (!reviewId.isNullOrEmpty()) {
                onReviewClick(reviewId)
            } else {
                onUserClick(notificacion.actorId)
            }
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
    tabs: List<NotificationTabUi>,
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
    Box(modifier = modifier.fillMaxSize()) {
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
            NotificationsTabs(
                tabs = tabs,
                selectedTabId = selectedTabId,
                onTabSelected = onTabSelected,
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
                            LanzamientoRow(
                                item = item,
                                onClick = { onItemClick(item) },
                            )
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
