package com.example.mixtapp.ui.screens.following

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.mixtapp.R
import com.example.mixtapp.data.local.LocalFollowingProvider
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.ui.screens.following.components.FollowingBackground
import com.example.mixtapp.ui.screens.following.components.FollowingList
import com.example.mixtapp.ui.screens.following.model.FollowingFilterUi
import com.example.mixtapp.ui.screens.following.model.followingFilters
import com.example.mixtapp.ui.theme.MixtappTheme
import com.example.mixtapp.ui.utils.LoadingBox

@Composable
fun FollowingScreen(
    followingViewModel: FollowingViewModel,
    onAuthorClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by followingViewModel.uiState.collectAsState()

    // Empieza a escuchar la primera vez que se muestra; despues los cambios llegan solos
    LaunchedEffect(Unit) {
        followingViewModel.getFollowing()
    }

    // Con una copia local, Kotlin sabe que no es nula despues de revisarla: no hace falta el !!
    val following = state.following

    when {
        state.isLoading -> {
            LoadingBox(modifier = modifier)
        }
        following == null -> {
            Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = stringResource(state.errorMessageRes ?: R.string.resenas_no_encontradas))
            }
        }
        else -> FollowingScreenContent(
            following = following,
            reviews = state.reviews,
            friendQuery = state.friendQuery,
            filters = state.filters,
            selectedFilterId = state.selectedFilterId,
            selectedStoryId = state.selectedStoryId,
            errorMessageRes = state.errorMessageRes,
            onFriendQueryChange = { followingViewModel.updateFriendQuery(friendQuery = it) },
            onFilterSelected = { followingViewModel.updateSelectedFilter(filtroId = it) },
            onStoryClick = { followingViewModel.updateSelectedStory(storyId = it) },
            onLikeClick = { followingViewModel.sendOrDeleteLike(reviewId = it) },
            onShareClick = { followingViewModel.compartirQuitar(reviewId = it) },
            onAuthorClick = onAuthorClick,
            onAlbumClick = onAlbumClick,
            onCommentsClick = onCommentsClick,
            onBackClick = onBackClick,
            modifier = modifier,
        )
    }
}

@Composable
fun FollowingScreenContent(
    following: FollowingUi,
    reviews: List<FollowingReviewUi>,
    friendQuery: String,
    filters: List<FollowingFilterUi>,
    selectedFilterId: String,
    selectedStoryId: String?,
    errorMessageRes: Int?,
    onFriendQueryChange: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onStoryClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onAuthorClick: (String) -> Unit,
    onAlbumClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FollowingBackground()

        FollowingList(
            following = following,
            reviews = reviews,
            friendQuery = friendQuery,
            filters = filters,
            selectedFilterId = selectedFilterId,
            selectedStoryId = selectedStoryId,
            errorMessageRes = errorMessageRes,
            onFriendQueryChange = onFriendQueryChange,
            onFilterSelected = onFilterSelected,
            onStoryClick = onStoryClick,
            onLikeClick = onLikeClick,
            onShareClick = onShareClick,
            onAuthorClick = onAuthorClick,
            onAlbumClick = onAlbumClick,
            onCommentsClick = onCommentsClick,
            onBackClick = onBackClick,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun FollowingScreenPreview() {
    val following = LocalFollowingProvider.following

    MixtappTheme(darkTheme = true) {
        FollowingScreenContent(
            following = following,
            reviews = following.reviews,
            friendQuery = "",
            filters = followingFilters,
            selectedFilterId = followingFilters.first().id,
            selectedStoryId = null,
            errorMessageRes = null,
            onFriendQueryChange = {},
            onFilterSelected = {},
            onStoryClick = {},
            onLikeClick = {},
            onShareClick = {},
            onAuthorClick = {},
            onAlbumClick = {},
            onCommentsClick = {},
            onBackClick = {},
        )
    }
}
