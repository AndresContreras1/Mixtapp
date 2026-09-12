package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.screens.following.model.FollowingStoryUi
import com.example.mixtapp.ui.theme.StoryGold

@Composable
fun FollowingStoriesRow(
    stories: List<FollowingStoryUi>,
    selectedStoryId: String?,
    onStoryClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 33.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(horizontal = 4.dp),
    ) {
        items(stories, key = { it.id }) { story ->
            FollowingStoryBubble(
                story = story,
                selected = story.id == selectedStoryId,
                onClick = { onStoryClick(story.id) },
            )
        }
    }
}

@Composable
private fun FollowingStoryBubble(
    story: FollowingStoryUi,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bubbleBackground = if (story.isAddAction) {
        Modifier.background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
    } else {
        Modifier.background(
            Brush.linearGradient(colors = listOf(MaterialTheme.colorScheme.primary, StoryGold, MaterialTheme.colorScheme.secondary))
        )
    }

    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(58.dp)
                .clip(CircleShape)
                .then(bubbleBackground)
                .border(
                    width = if (selected) 2.dp else 0.dp,
                    color = if (selected) MaterialTheme.colorScheme.onSurfaceVariant else Color.Transparent,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = story.initials,
                color = if (story.isAddAction) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.58f) else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = if (story.isAddAction) 36.sp else 17.sp,
                fontWeight = FontWeight.Black,
            )
        }

        Text(
            text = story.label,
            modifier = Modifier.padding(top = 7.dp),
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.76f),
            fontSize = 13.sp,
            lineHeight = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
