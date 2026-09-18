package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mixtapp.data.model.FollowingReviewUi
import com.example.mixtapp.data.model.FollowingUi
import com.example.mixtapp.ui.components.ErrorMessage
import com.example.mixtapp.ui.screens.following.model.FollowingFilterUi

@Composable
fun FollowingList(
    following: FollowingUi,
    reviews: List<FollowingReviewUi>,
    friendQuery: String,
    filters: List<FollowingFilterUi>,
    selectedFilterId: String,
    selectedStoryId: String?,
    likedReviewIds: Set<String>,
    sharedReviewIds: Set<String>,
    errorMessageRes: Int?,
    onFriendQueryChange: (String) -> Unit,
    onFilterSelected: (String) -> Unit,
    onStoryClick: (String) -> Unit,
    onLikeClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onCommentsClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
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
                filters = filters,
                selectedFilterId = selectedFilterId,
                onFilterSelected = onFilterSelected,
            )
        }

        item {
            ErrorMessage(
                messageRes = errorMessageRes,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        items(reviews, key = { it.id }) { review ->
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
