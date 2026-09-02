package com.example.mixtapp.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalFriendActivityProvider
import com.example.mixtapp.data.local.LocalSongReviewProvider
import com.example.mixtapp.ui.components.AppBackground
import com.example.mixtapp.ui.screens.home.components.FilterChips
import com.example.mixtapp.ui.screens.home.components.FriendsActivity
import com.example.mixtapp.ui.screens.home.components.HomeHeader
import com.example.mixtapp.ui.screens.home.components.PopularAlbums
import com.example.mixtapp.ui.screens.home.components.SectionTitle
import com.example.mixtapp.ui.screens.home.components.TrendingCard
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
    modifier: Modifier = Modifier,
) {
    val filters = listOf(
        stringResource(R.string.for_you),
        stringResource(R.string.trending),
        stringResource(R.string.friends)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        AppBackground()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 28.dp)
            ) {
                HomeHeader(
                    onSearchClick = onSearchClick,
                    onProfileClick = onProfileClick
                )

                Spacer(modifier = Modifier.height(24.dp))

                FilterChips(
                    filters = filters,
                    selected = filters[selectedFilterIndex],
                    onFilterSelected = { onFilterSelected(filters.indexOf(it)) }
                )

                Spacer(modifier = Modifier.height(28.dp))

                TrendingCard(
                    album = trending,
                    onClick = onAlbumClick
                )

                Spacer(modifier = Modifier.height(28.dp))

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

                FriendsActivity(activity = friendActivity)
            }

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
        onFollowingClick = {}
    )
}
