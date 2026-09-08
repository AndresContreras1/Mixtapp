package com.example.mixtapp.ui.screens.following

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.data.local.LocalFollowingProvider
import com.example.mixtapp.ui.screens.following.components.FollowingBackground
import com.example.mixtapp.ui.screens.following.components.FollowingList
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
            FollowingList(
                following = following,
                friendQuery = friendQuery,
                selectedFilter = selectedFilter,
                selectedStoryId = selectedStoryId,
                likedReviewIds = likedReviewIds,
                sharedReviewIds = sharedReviewIds,
                onFriendQueryChange = onFriendQueryChange,
                onFilterSelected = onFilterSelected,
                onStoryClick = onStoryClick,
                onLikeClick = onLikeClick,
                onShareClick = onShareClick,
                onCommentsClick = onCommentsClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )

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
