package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink

@Composable
fun FollowingReviewActions(
    likes: Int,
    comments: Int,
    isLiked: Boolean,
    isShared: Boolean,
    onLikeClick: () -> Unit,
    onShareClick: () -> Unit,
    onCommentsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = stringResource(R.string.like_review),
            tint = if (isLiked) PrimaryPink else PalePink.copy(alpha = 0.55f),
            modifier = Modifier
                .size(16.dp)
                .clickable { onLikeClick() },
        )
        Text(
            text = (likes + if (isLiked) 1 else 0).toString(),
            color = PalePink.copy(alpha = 0.55f),
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.width(8.dp))

        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = stringResource(R.string.open_discussion),
            tint = PalePink.copy(alpha = 0.55f),
            modifier = Modifier
                .size(15.dp)
                .clickable { onCommentsClick() },
        )
        Text(
            text = comments.toString(),
            color = PalePink.copy(alpha = 0.55f),
            fontSize = 12.sp,
        )

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.Outlined.Share,
            contentDescription = stringResource(R.string.share_review),
            tint = if (isShared) PrimaryPink else PalePink.copy(alpha = 0.6f),
            modifier = Modifier
                .size(18.dp)
                .clickable { onShareClick() },
        )
    }
}
