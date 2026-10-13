package com.example.mixtapp.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalFriendActivityProvider
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.ui.screens.home.components.HomeSections
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi
import com.example.mixtapp.ui.screens.home.model.homeFilters
import com.example.mixtapp.ui.theme.*
import com.example.mixtapp.ui.utils.AppBackground

@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel,
    onAlbumClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by homeViewModel.uiState.collectAsState()

    when {
        state.isLoading -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        // Sin albumes no hay nada que mostrar: solo el motivo del error
        state.errorMessageRes != null && state.todos.isEmpty() -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.error_cargar_contenido))
            }
        }
        else -> HomeScreenContent(
            albums = state.albums,
            todos = state.todos,
            trending = state.trending,
            friendActivity = state.friendActivity,
            filters = state.filters,
            selectedFilterId = state.selectedFilterId,
            profileImageUrl = state.profileImageUrl,
            errorMessageRes = state.errorMessageRes,
            onFilterSelected = { homeViewModel.updateSelectedFilter(filtroId = it) },
            onAlbumClick = onAlbumClick,
            onSearchClick = onSearchClick,
            onProfileClick = onProfileClick,
            onFollowingClick = onFollowingClick,
            onNotificationsClick = onNotificationsClick,
            modifier = modifier
        )
    }
}

@Composable
fun HomeScreenContent(
    albums: List<SongReviewUi>,
    todos: List<SongReviewUi>,
    trending: SongReviewUi?,
    friendActivity: FriendActivityUi?,
    filters: List<HomeFilterUi>,
    selectedFilterId: String,
    profileImageUrl: String,
    errorMessageRes: Int?,
    onFilterSelected: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        AppBackground()

        HomeSections(
            albums = albums,
            todos = todos,
            trending = trending,
            friendActivity = friendActivity,
            filters = filters,
            selectedFilterId = selectedFilterId,
            profileImageUrl = profileImageUrl,
            errorMessageRes = errorMessageRes,
            onFilterSelected = onFilterSelected,
            onAlbumClick = onAlbumClick,
            onSearchClick = onSearchClick,
            onProfileClick = onProfileClick,
            onFollowingClick = onFollowingClick,
            onNotificationsClick = onNotificationsClick,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    MixtappTheme(darkTheme = true) {
        HomeScreenContent(
            albums = LocalSongReviewProvider.popularSongs,
            todos = LocalSongReviewProvider.songs,
            trending = LocalSongReviewProvider.trendingSong,
            friendActivity = LocalFriendActivityProvider.friendActivity,
            filters = homeFilters,
            selectedFilterId = homeFilters.first().id,
            profileImageUrl = "",
            errorMessageRes = null,
            onFilterSelected = {},
            onAlbumClick = {},
            onSearchClick = {},
            onProfileClick = {},
            onFollowingClick = {},
            onNotificationsClick = {}
        )
    }
}
