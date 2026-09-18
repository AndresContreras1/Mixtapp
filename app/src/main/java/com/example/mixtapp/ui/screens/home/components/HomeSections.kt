package com.example.mixtapp.ui.screens.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.mixtapp.R
import com.example.mixtapp.data.model.FriendActivityUi
import com.example.mixtapp.data.model.SongReviewUi
import com.example.mixtapp.ui.components.ErrorMessage
import com.example.mixtapp.ui.screens.home.model.HomeFilterUi

@Composable
fun HomeSections(
    albums: List<SongReviewUi>,
    trending: SongReviewUi,
    friendActivity: FriendActivityUi,
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
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 28.dp)
    ) {
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

        TrendingCard(
            songReview = trending,
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
