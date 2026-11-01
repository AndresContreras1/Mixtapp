package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalAlbumReviewsProvider
import com.example.mixtapp.data.local.LocalFriendActivityProvider
import com.example.mixtapp.data.model.AlbumReviewsUi
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.ui.screens.home.model.FILTRO_SEGUIDOS
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi
import com.example.mixtapp.ui.screens.home.model.homeFilters
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.ErrorMessage
import com.example.mixtapp.ui.utils.ReviewAlbumRow

@Composable
fun HomeSections(
    albums: List<AlbumReviewsUi>,
    todos: List<AlbumReviewsUi>,
    trending: AlbumReviewsUi?,
    resenasDeSeguidos: List<FriendActivityUi>,
    filters: List<HomeFilterUi>,
    selectedFilterId: String,
    // Con el filtro de seguidos solo se muestran las resenas de quienes sigo
    soloSeguidos: Boolean,
    profileImageUrl: String,
    errorMessageRes: Int?,
    onFilterSelected: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 28.dp)
    ) {
        item {
            HomeHeader(
                profileImageUrl = profileImageUrl,
                onSearchClick = onSearchClick,
                onProfileClick = onProfileClick,
                onNotificationsClick = onNotificationsClick
            )

            Spacer(modifier = Modifier.height(24.dp))

            FilterChips(
                filters = filters,
                selectedId = selectedFilterId,
                onFilterSelected = onFilterSelected
            )

            ErrorMessage(
                messageRes = errorMessageRes,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))
        }

        if (soloSeguidos) {
            item {
                SectionTitle(
                    title = stringResource(R.string.resenas_de_seguidos),
                    onSeeAllClick = onFollowingClick
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (resenasDeSeguidos.isEmpty()) {
                    FriendsActivityEmpty(onFollowingClick = onFollowingClick)
                }
            }

            // Cada resena de un usuario que sigo, de la mas reciente a la mas antigua
            items(resenasDeSeguidos.size) { index ->
                FriendsActivity(
                    activity = resenasDeSeguidos[index],
                    onClick = onAlbumClick,
                    onAuthorClick = onAuthorClick,
                    onCommentsClick = onCommentsClick,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }
        } else {
            item {
                if (trending != null) {
                    TrendingCard(
                        albumReviews = trending,
                        onClick = onAlbumClick
                    )

                    Spacer(modifier = Modifier.height(28.dp))
                }

                SectionTitle(
                    title = stringResource(R.string.popular_albums),
                    onSeeAllClick = onSearchClick
                )

                Spacer(modifier = Modifier.height(14.dp))

                PopularAlbums(
                    albums = albums,
                    onAlbumClick = onAlbumClick
                )

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle(
                    title = stringResource(R.string.friends_activity),
                    onSeeAllClick = onFollowingClick
                )

                Spacer(modifier = Modifier.height(14.dp))

                // La resena mas reciente de alguien que sigo
                val ultimaDeSeguidos = resenasDeSeguidos.firstOrNull()

                if (ultimaDeSeguidos != null) {
                    FriendsActivity(
                        activity = ultimaDeSeguidos,
                        onClick = onAlbumClick,
                        onAuthorClick = onAuthorClick,
                        onCommentsClick = onCommentsClick
                    )
                } else {
                    FriendsActivityEmpty(
                        onFollowingClick = onFollowingClick
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                SectionTitle(
                    title = stringResource(R.string.todos_los_albumes),
                    onSeeAllClick = onSearchClick
                )

                Spacer(modifier = Modifier.height(6.dp))
            }

            // Todos los albumes guardados, uno debajo del otro
            items(todos.size) { index ->
                ReviewAlbumRow(
                    cover = todos[index].album.cover,
                    albumTitle = todos[index].album.title,
                    artistName = todos[index].album.artist,
                    rating = todos[index].estrellas,
                    coverSize = 64.dp,
                    coverCorner = 10.dp,
                    starSize = 14.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAlbumClick(todos[index].album.id) }
                        .padding(vertical = 10.dp)
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun HomeSectionsPreview() {
    MixtappTheme(darkTheme = true) {
        HomeSections(
            albums = LocalAlbumReviewsProvider.popularAlbums,
            todos = LocalAlbumReviewsProvider.albumReviews,
            trending = LocalAlbumReviewsProvider.trendingAlbum,
            resenasDeSeguidos = LocalFriendActivityProvider.friendActivities,
            filters = homeFilters,
            selectedFilterId = homeFilters.first().id,
            soloSeguidos = false,
            profileImageUrl = "",
            errorMessageRes = null,
            onFilterSelected = {},
            onAlbumClick = {},
            onAuthorClick = {},
            onCommentsClick = {},
            onSearchClick = {},
            onProfileClick = {},
            onFollowingClick = {},
            onNotificationsClick = {},
        )
    }
}

@Composable
@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
fun HomeSectionsSoloSeguidosPreview() {
    MixtappTheme(darkTheme = true) {
        HomeSections(
            albums = LocalAlbumReviewsProvider.popularAlbums,
            todos = LocalAlbumReviewsProvider.albumReviews,
            trending = LocalAlbumReviewsProvider.trendingAlbum,
            resenasDeSeguidos = LocalFriendActivityProvider.friendActivities,
            filters = homeFilters,
            selectedFilterId = FILTRO_SEGUIDOS,
            soloSeguidos = true,
            profileImageUrl = "",
            errorMessageRes = null,
            onFilterSelected = {},
            onAlbumClick = {},
            onAuthorClick = {},
            onCommentsClick = {},
            onSearchClick = {},
            onProfileClick = {},
            onFollowingClick = {},
            onNotificationsClick = {},
        )
    }
}
