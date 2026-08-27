package com.example.mixtapp.ui.screens.discussion.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.ui.screens.discussion.model.DiscussionCommentUi
import com.example.mixtapp.ui.theme.CircleBerry
import com.example.mixtapp.ui.theme.CircleWine
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.PrimaryPink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun ThreadCommentItem(
    comment: DiscussionCommentUi,
    isLiked: Boolean,
    isReplying: Boolean,
    onLikeClick: () -> Unit,
    onReplyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (comment.isReply) 38.dp else 0.dp,
                top = 7.dp,
                bottom = 5.dp,
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (comment.isReply) CircleWine else CircleBerry),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = comment.initials,
                    color = PalePink.copy(alpha = 0.62f),
                    fontSize = 15.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                )
            }

            if (!comment.isReply) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(48.dp)
                        .background(PrimaryPink.copy(alpha = 0.45f))
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.author,
                    color = PalePink.copy(alpha = 0.58f),
                    fontSize = 20.sp,
                    lineHeight = 24.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Black,
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = comment.timeAgo,
                    color = TextPink.copy(alpha = 0.55f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                )
            }

            Text(
                text = comment.content,
                modifier = Modifier.padding(top = 6.dp),
                color = TextPink.copy(alpha = 0.72f),
                fontSize = 10.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Medium,
            )

            Row(
                modifier = Modifier.padding(top = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Like comment",
                    tint = if (isLiked) PrimaryPink else TextPink.copy(alpha = 0.62f),
                    modifier = Modifier
                        .size(12.dp)
                        .clickable { onLikeClick() },
                )
                Text(
                    text = (comment.likes + if (isLiked) 1 else 0).toString(),
                    color = TextPink.copy(alpha = 0.62f),
                    fontSize = 10.sp,
                )

                Spacer(modifier = Modifier.width(18.dp))

                Text(
                    text = if (isReplying) "Replying" else "Reply",
                    color = if (isReplying) PrimaryPink else TextPink.copy(alpha = 0.62f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onReplyClick() },
                )
            }
        }
    }
}
