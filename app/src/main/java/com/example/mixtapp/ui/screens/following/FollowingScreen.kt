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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.local.LocalFollowingProvider
import com.example.mixtapp.ui.screens.following.components.FollowingBackground
import com.example.mixtapp.ui.screens.following.components.FollowingBottomBar
import com.example.mixtapp.ui.screens.following.components.FollowingFilters
import com.example.mixtapp.ui.screens.following.components.FollowingHeader
import com.example.mixtapp.ui.screens.following.components.FollowingReviewCard
import com.example.mixtapp.ui.screens.following.components.FollowingStoriesRow
import com.example.mixtapp.ui.screens.following.model.FollowingUi
import com.example.mixtapp.ui.theme.DeepBackground

@Composable
fun FollowingScreen(
    modifier: Modifier = Modifier,
    following: FollowingUi = LocalFollowingProvider.following,
) {
    var friendQuery by rememberSaveable { mutableStateOf("") }
    var selectedFilter by rememberSaveable { mutableStateOf(following.filters.first()) }
    var selectedStoryId by rememberSaveable { mutableStateOf<String?>(null) }
    var likedReviewIds by rememberSaveable {
        mutableStateOf(following.reviews.filter { it.isLiked }.map { it.id })
    }
    var sharedReviewIds by rememberSaveable {
        mutableStateOf(following.reviews.filter { it.isShared }.map { it.id })
    }

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
                        onFriendQueryChange = { friendQuery = it },
                    )
                }

                item {
                    FollowingStoriesRow(
                        stories = following.stories,
                        selectedStoryId = selectedStoryId,
                        onStoryClick = { selectedStoryId = it },
                    )
                }

                item {
                    FollowingFilters(
                        filters = following.filters,
                        selectedFilter = selectedFilter,
                        onFilterSelected = { selectedFilter = it },
                    )
                }

                items(following.reviews, key = { it.id }) { review ->
                    FollowingReviewCard(
                        review = review,
                        isLiked = review.id in likedReviewIds,
                        isShared = review.id in sharedReviewIds,
                        onLikeClick = {
                            likedReviewIds = if (review.id in likedReviewIds) {
                                likedReviewIds - review.id
                            } else {
                                likedReviewIds + review.id
                            }
                        },
                        onShareClick = {
                            sharedReviewIds = if (review.id in sharedReviewIds) {
                                sharedReviewIds - review.id
                            } else {
                                sharedReviewIds + review.id
                            }
                        },
                    )
                }
            }

            FollowingBottomBar()
        }
    }
}

@Preview(showBackground = true, device = "spec:width=393dp,height=852dp")
@Composable
fun FollowingScreenPreview() {
    FollowingScreen()
}
