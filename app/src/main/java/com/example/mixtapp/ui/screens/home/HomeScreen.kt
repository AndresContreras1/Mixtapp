package com.example.mixtapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.data.local.LocalFriendActivityProvider
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.ui.components.AppBackground
import com.example.mixtapp.ui.screens.home.components.HomeSections
import com.example.mixtapp.ui.screens.home.model.FriendActivityUi
import com.example.mixtapp.ui.screens.songreview.model.SongReviewUi
import com.example.mixtapp.ui.theme.*

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

    if (state.trending != null && state.friendActivity != null) {
        HomeScreenContent(
            albums = state.albums,
            trending = state.trending!!,
            friendActivity = state.friendActivity!!,
            selectedFilterIndex = state.selectedFilterIndex,
            onFilterSelected = { homeViewModel.updateSelectedFilter(index = it) },
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
    trending: SongReviewUi,
    friendActivity: FriendActivityUi,
    selectedFilterIndex: Int,
    onFilterSelected: (Int) -> Unit,
    onAlbumClick: (String) -> Unit,
    onSearchClick: () -> Unit,
    onProfileClick: () -> Unit,
    onFollowingClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        AppBackground()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            HomeSections(
                albums = albums,
                trending = trending,
                friendActivity = friendActivity,
                selectedFilterIndex = selectedFilterIndex,
                onFilterSelected = onFilterSelected,
                onAlbumClick = onAlbumClick,
                onSearchClick = onSearchClick,
                onProfileClick = onProfileClick,
                onFollowingClick = onFollowingClick,
                onNotificationsClick = onNotificationsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )

        }
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    HomeScreenContent(
        albums = LocalSongReviewProvider.popularSongs,
        trending = LocalSongReviewProvider.trendingSong,
        friendActivity = LocalFriendActivityProvider.friendActivity,
        selectedFilterIndex = 0,
        onFilterSelected = {},
        onAlbumClick = {},
        onSearchClick = {},
        onProfileClick = {},
        onFollowingClick = {},
        onNotificationsClick = {}
    )
}
