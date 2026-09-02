package com.example.mixtapp.ui.screens.following

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalFollowingProvider
import com.example.mixtapp.ui.screens.following.components.FollowingBackground
import com.example.mixtapp.ui.screens.following.components.FollowingFilters
import com.example.mixtapp.ui.screens.following.components.FollowingHeader
import com.example.mixtapp.ui.screens.following.components.FollowingReviewCard
import com.example.mixtapp.ui.screens.following.components.FollowingStoriesRow
import com.example.mixtapp.ui.screens.following.model.FollowingUi
import com.example.mixtapp.ui.theme.DeepBackground

@Composable
fun FollowingScreen(
    followingViewModel: FollowingViewModel,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by followingViewModel.uiState.collectAsState()

    if (state.following != null) {
        FollowingScreenContent(
            following = state.following!!,
            friendQuery = state.friendQuery,
            selectedFilter = state.selectedFilter,
            selectedStoryId = state.selectedStoryId,
            likedReviewIds = state.likedReviewIds,
            sharedReviewIds = state.sharedReviewIds,
            onFriendQueryChange = { followingViewModel.updateFriendQuery(friendQuery = it) },
            onFilterSelected = { followingViewModel.updateSelectedFilter(filtro = it) },
            onStoryClick = { followingViewModel.updateSelectedStory(storyId = it) },
            onLikeClick = { followingViewModel.darQuitarLike(reviewId = it) },
            onShareClick = { followingViewModel.compartirQuitar(reviewId = it) },
            onCommentsClick = onCommentsClick,
            modifier = modifier,
        )
    }
}

@Composable
fun FollowingScreenContent(
    following: FollowingUi,
    friendQuery: String,
    selectedFilter: String,
    selectedStoryId: String?,
    likedReviewIds: List<String>,
    sharedReviewIds: List<String>,
    onFriendQueryChange: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onStoryClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBackground)
    ) {
        FollowingBackground()

        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(start = 24.dp, end = 24.dp, bottom = 28.dp)
            ) {
                item {
                    FollowingHeader(
                        followingCount = following.followingCount,
                        followersCount = following.followersCount,
                        friendQuery = friendQuery,
                        onFriendQueryChange = onFriendQueryChange,
                    )
                }

                item {
                    FollowingStoriesRow(
                        stories = following.stories,
                        selectedStoryId = selectedStoryId,
                        onStoryClick = onStoryClick,
                    )
                }

                item {
                    FollowingFilters(
                        filters = following.filters,
                        selectedFilter = selectedFilter,
                        onFilterSelected = onFilterSelected,
                    )
                }

                items(following.reviews, key = { it.id }) { review ->
                    FollowingReviewCard(
                        review = review,
                        isLiked = review.id in likedReviewIds,
                        isShared = review.id in sharedReviewIds,
                        onLikeClick = { onLikeClick(review.id) },
                        onShareClick = { onShareClick(review.id) },
                        onCommentsClick = { onCommentsClick(review.id) },
                    )
                }
            }

        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun FollowingScreenPreview() {
    val following = LocalFollowingProvider.following

    FollowingScreenContent(
        following = following,
        friendQuery = "",
        selectedFilter = following.filters.first(),
        selectedStoryId = null,
        likedReviewIds = emptyList(),
        sharedReviewIds = emptyList(),
        onFriendQueryChange = {},
        onFilterSelected = {},
        onStoryClick = {},
        onLikeClick = {},
        onShareClick = {},
        onCommentsClick = {},
    )
}
