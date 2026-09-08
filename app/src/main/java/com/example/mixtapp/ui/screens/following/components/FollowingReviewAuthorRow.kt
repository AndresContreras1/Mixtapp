package com.example.mixtapp.ui.screens.following.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mixtapp.R
import com.example.mixtapp.ui.screens.following.model.FollowingReviewUi
import com.example.mixtapp.ui.theme.CircleBerry
import com.example.mixtapp.ui.theme.CircleWine
import com.example.mixtapp.ui.theme.PalePink
import com.example.mixtapp.ui.theme.TextPink

@Composable
fun FollowingReviewAuthorRow(
    review: FollowingReviewUi,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(CircleBerry, Color(0xFFF5C25E), CircleWine)
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = review.reviewerInitials,
                color = PalePink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = review.reviewerName,
            color = PalePink,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = stringResource(R.string.reviewed),
            color = TextPink.copy(alpha = 0.56f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            text = review.reviewedAt,
            color = TextPink.copy(alpha = 0.58f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
