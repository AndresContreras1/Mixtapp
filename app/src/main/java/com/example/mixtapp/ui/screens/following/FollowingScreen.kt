package com.example.mixtapp.ui.screens.following

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalPreviewProvider
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.data.model.ItemResumenUi
import com.example.mixtapp.data.model.ReviewUi
import com.example.mixtapp.ui.screens.following.components.FollowingUsersRow
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.AppBackground
import com.example.mixtapp.ui.utils.AppTextField
import com.example.mixtapp.ui.utils.EmptyMessage
import com.example.mixtapp.ui.utils.ErrorBox
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.LoadingBox
import com.example.mixtapp.ui.utils.ReviewCard
import com.example.mixtapp.ui.utils.ScreenTitle

@Composable
fun FollowingScreen(
    followingViewModel: FollowingViewModel,
    onAuthorClick: (String) -> Unit,
    onItemClick: (String, String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by followingViewModel.uiState.collectAsState()

    FollowingScreenContent(
        following = state.following,
        reviews = state.reviews,
        friendQuery = state.friendQuery,
        isLoading = state.isLoading,
        errorMessageRes = state.errorMessageRes,
        errorAccionRes = state.errorAccionRes,
        onFriendQueryChange = { followingViewModel.updateFriendQuery(friendQuery = it) },
        onLikeClick = { followingViewModel.sendOrDeleteLike(reviewId = it) },
        onAuthorClick = onAuthorClick,
        onItemClick = { item -> onItemClick(item.tipo, item.id) },
        onCommentsClick = onCommentsClick,
        onBackClick = onBackClick,
        modifier = modifier,
    )
}

@Composable
fun FollowingScreenContent(
    following: FollowingUi?,
    reviews: List<ReviewUi>,
    friendQuery: String,
    isLoading: Boolean,
    @StringRes errorMessageRes: Int?,
    @StringRes errorAccionRes: Int?,
    onFriendQueryChange: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onItemClick: (ItemResumenUi) -> Unit,
    onCommentsClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            // El titulo queda fijo para poder volver aunque falle la carga
            ScreenTitle(
                title = stringResource(R.string.following_title),
                onBackClick = onBackClick,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )

            when {
                isLoading -> LoadingBox()
                // Sin datos solo se muestra el motivo del error
                following == null -> ErrorBox(messageRes = errorMessageRes ?: R.string.error_cargar_contenido)
                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(bottom = 24.dp),
                ) {
                    item {
                        Text(
                            text = stringResource(
                                R.string.following_followers_count,
                                following.followingCount,
                                following.followersCount,
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                        )
                    }

                    // Si todavia no sigo a nadie no se deja la fila vacia
                    if (following.seguidos.isNotEmpty()) {
                        item {
                            FollowingUsersRow(
                                seguidos = following.seguidos,
                                onUserClick = onAuthorClick,
                                modifier = Modifier.padding(bottom = 16.dp),
                            )
                        }
                    }

                    item {
                        AppTextField(
                            value = friendQuery,
                            onValueChange = onFriendQueryChange,
                            placeholder = stringResource(R.string.find_friends),
                            leadingIcon = Icons.Outlined.Search,
                            modifier = Modifier.padding(horizontal = 20.dp),
                        )
                    }

                    item {
                        ErrorMessage(
                            messageRes = errorAccionRes,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }

                    if (reviews.isEmpty()) {
                        item {
                            EmptyMessage(
                                messageRes = R.string.sin_actividad_seguidos,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    items(reviews, key = { it.id }) { review ->
                        ReviewCard(
                            review = review,
                            onAuthorClick = onAuthorClick,
                            onLikeClick = onLikeClick,
                            onCommentsClick = onCommentsClick,
                            mostrarItem = true,
                            onItemClick = onItemClick,
                            modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun FollowingScreenPreview() {
    val following = LocalPreviewProvider.following

    MixtappTheme(darkTheme = true) {
        FollowingScreenContent(
            following = following,
            reviews = following.reviews,
            friendQuery = "",
            isLoading = false,
            errorMessageRes = null,
            errorAccionRes = null,
            onFriendQueryChange = {},
            onLikeClick = {},
            onAuthorClick = {},
            onItemClick = {},
            onCommentsClick = {},
            onBackClick = {},
        )
    }
}
